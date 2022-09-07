package com.jocoos.mybeautip.member.order;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.MybeautipException;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsOptionService;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.cart.CartRepository;
import com.jocoos.mybeautip.member.coupon.MemberCoupon;
import com.jocoos.mybeautip.member.coupon.MemberCouponRepository;
import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.member.point.MemberPointRepository;
import com.jocoos.mybeautip.member.point.MemberPointService;
import com.jocoos.mybeautip.member.revenue.RevenuePayment;
import com.jocoos.mybeautip.member.revenue.RevenuePaymentService;
import com.jocoos.mybeautip.member.revenue.RevenueRepository;
import com.jocoos.mybeautip.member.revenue.RevenueService;
import com.jocoos.mybeautip.notification.LegacyNotificationService;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.restapi.OrderController;
import com.jocoos.mybeautip.support.payment.IamportService;
import com.jocoos.mybeautip.support.payment.PaymentData;
import com.jocoos.mybeautip.support.payment.PaymentResponse;
import com.jocoos.mybeautip.support.slack.SlackService;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoGoods;
import com.jocoos.mybeautip.video.VideoGoodsRepository;
import com.jocoos.mybeautip.video.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.global.exception.ErrorCode.COUPON_NOT_FOUND;
import static com.jocoos.mybeautip.member.billing.MemberBillingService.MERCHANT_BILLING_PREFIX;

@Slf4j
@Service
public class OrderService {

    private static final List<String> EVENT_GOODS = Arrays.asList("1000037476", "1000037435", "1000037727", "1000037448");
    private static final Long EVENT_COUPON_ID = 4L;
    private static final String CANNOT_USE_COUPON_WITH_POINT = "order.not_use_coupon_with_point";
    private static final String INVALID_GOODS_FOR_COUPON = "order.invalid_goods_for_coupon";
    private static final String GOODS_NOT_FOUND = "goods.not_found";
    private static final String POINT_BAD_REQUEST = "order.point_bad_rqeust";
    private static final String POINT_NOT_ENOUGH = "order.point_not_enough";
    private static final String POINT_BAD_REQUEST_MIN_PRICE_CONDITION = "order.price_not_enough_to_use_point";
    private static final int MIN_PRICE_TO_USE_POINT = 100;
    private static final int REVENUE_DURATION_AFTER_LIVE_ENDED = 300 * 1000;  // 5 min
    private final static String MERCHANT_PREFIX = "mybeautip_";
    private final SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmssSSS");
    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final DeliveryRepository deliveryRepository;
    private final PaymentRepository paymentRepository;
    private final PurchaseRepository purchaseRepository;
    private final OrderInquiryRepository orderInquiryRepository;
    private final MemberCouponRepository memberCouponRepository;
    private final GoodsRepository goodsRepository;
    private final VideoGoodsRepository videoGoodsRepository;
    private final CartRepository cartRepository;
    private final RevenueRepository revenueRepository;
    private final VideoRepository videoRepository;
    private final MemberPointRepository memberPointRepository;
    private final RevenueService revenueService;
    private final RevenuePaymentService revenuePaymentService;
    private final MemberPointService memberPointService;
    private final IamportService iamportService;
    private final MessageService messageService;
    private final SlackService slackService;
    private final LegacyNotificationService legacyNotificationService;
    private final GoodsOptionService goodsOptionService;
    @Value("${mybeautip.point.minimum}")
    private int minimumPoint;

    public OrderService(OrderRepository orderRepository,
                        MemberRepository memberRepository,
                        DeliveryRepository deliveryRepository,
                        PaymentRepository paymentRepository,
                        PurchaseRepository purchaseRepository,
                        OrderInquiryRepository orderInquiryRepository,
                        MemberCouponRepository memberCouponRepository,
                        GoodsRepository goodsRepository,
                        VideoGoodsRepository videoGoodsRepository,
                        CartRepository cartRepository,
                        RevenueRepository revenueRepository,
                        VideoRepository videoRepository,
                        MemberPointRepository memberPointRepository,
                        RevenueService revenueService,
                        RevenuePaymentService revenuePaymentService,
                        MemberPointService memberPointService,
                        IamportService iamportService,
                        MessageService messageService,
                        SlackService slackService,
                        LegacyNotificationService legacyNotificationService,
                        GoodsOptionService goodsOptionService) {
        this.orderRepository = orderRepository;
        this.memberRepository = memberRepository;
        this.deliveryRepository = deliveryRepository;
        this.paymentRepository = paymentRepository;
        this.purchaseRepository = purchaseRepository;
        this.orderInquiryRepository = orderInquiryRepository;
        this.memberCouponRepository = memberCouponRepository;
        this.goodsRepository = goodsRepository;
        this.videoGoodsRepository = videoGoodsRepository;
        this.cartRepository = cartRepository;
        this.revenueRepository = revenueRepository;
        this.videoRepository = videoRepository;
        this.memberPointRepository = memberPointRepository;
        this.revenueService = revenueService;
        this.revenuePaymentService = revenuePaymentService;
        this.memberPointService = memberPointService;
        this.iamportService = iamportService;
        this.messageService = messageService;
        this.slackService = slackService;
        this.legacyNotificationService = legacyNotificationService;
        this.goodsOptionService = goodsOptionService;
    }

    @Transactional
    public Order create(OrderController.CreateOrderRequest request, Member member, String lang) {
        Order order = new Order();
        if ("bank".equals(request.getPayment().getMethod())) {
            order.setState(Order.State.ORDERED);
        }

        BeanUtils.copyProperties(request, order);
        order.setMethod(request.getPayment().getMethod());
        order.setPrice(request.getPayment().getPrice());
        order.setGoodsCount(Optional.of(request.getPurchases()).map(List::size).orElse(0));
        order.setNumber(orderNumber());

        if (request.getPoint() > 0) {
            if (request.getPoint() < minimumPoint) {
                throw new BadRequestException(messageService.getMessage(POINT_BAD_REQUEST, lang));
            }

            if (member.getPoint() < request.getPoint()) {
                throw new BadRequestException(messageService.getMessage(POINT_NOT_ENOUGH, lang));
            }
        }

        // Modify member phoneNumber with Order buyerPhoneNumber
        if (request.getBuyerPhoneNumber() != null && !request.getBuyerPhoneNumber().equals(member.getPhoneNumber())) {
            member.setPhoneNumber(request.getBuyerPhoneNumber());
            memberRepository.save(member);
        }

        if (request.getCouponId() != null) {
            MemberCoupon memberCoupon = memberCouponRepository.findById(request.getCouponId()).orElseThrow(() -> new BadRequestException(COUPON_NOT_FOUND, "invalid member coupon id"));

            if (memberCoupon.getCoupon().getId().equals(EVENT_COUPON_ID)) {
                if (request.getPoint() > 0) {
                    throw new BadRequestException(messageService.getMessage(CANNOT_USE_COUPON_WITH_POINT, lang));
                }

                List<String> goodsNoList =
                        request.getPurchases().stream().map(p -> p.getGoodsNo()).collect(Collectors.toList());
                log.info("{}", goodsNoList);

                for (String gno : goodsNoList) {
                    if (!EVENT_GOODS.contains(gno)) {
                        throw new BadRequestException(messageService.getMessage(INVALID_GOODS_FOR_COUPON, lang));
                    }
                }
            }

            order.setMemberCoupon(memberCoupon);
        }

        // remove PurchaseRequest
        order.setPurchases(null);
        log.debug("before order: {}", order);

        orderRepository.save(order);
        log.debug("after order: {}", order);

        Delivery delivery = new Delivery(order);
        BeanUtils.copyProperties(request.getDelivery(), delivery);
        log.debug("delivery: {}", delivery);

        deliveryRepository.save(delivery);

        Payment payment = new Payment(order);
        BeanUtils.copyProperties(request.getPayment(), payment);
        log.debug("payment: {}", payment);

        paymentRepository.save(payment);


        List<Purchase> purchases = new ArrayList<>();
        request.getPurchases().forEach(p -> goodsRepository.findByGoodsNo(p.getGoodsNo())
                .map(goods -> {
                    Purchase purchase = new Purchase(order.getId(), goods);
                    BeanUtils.copyProperties(p, purchase);

                    purchase.setVideoId(order.getVideoId());
                    purchase.setOnLive(order.getOnLive());
                    purchase.setTotalPrice((long) (p.getQuantity() * p.getGoodsPrice()));

                    String optionNames = goodsOptionService.getGoodsOptionNames(p.getGoodsNo(), p.getOptionId());
                    log.debug("optionNames: {}", optionNames);
                    if (!StringUtils.isBlank(optionNames)) {
                        purchase.setOptionValue(optionNames);
                    }
                    purchases.add(purchase);
                    return Optional.empty();
                })
                .orElseThrow(() -> new NotFoundException(messageService.getMessage(GOODS_NOT_FOUND, lang))));

        log.debug("purchases: {}", purchases);
        order.setPurchases(purchases);
        order.setDelivery(delivery);
        order.setPayment(payment);
        orderRepository.save(order);

        return order;
    }

    @Transactional
    public void completeZeroOrder(Order order) {
        checkCoupon(order);

        paymentRepository.findById(order.getId())
                .map(payment -> {
                    payment.setMessage("0");
                    payment.setState(Payment.STATE_READY | Payment.STATE_PAID);
                    return paymentRepository.save(payment);
                })
                .orElseThrow(() -> new NotFoundException("invalid order id or payment id"));
        completeOrder(order);
        slackService.sendForOrder(order);
    }

    @Transactional
    public Order create2(Order order, String customerId, Member member, String lang) {
        checkCoupon(order);

        String name;
        if (order.getPurchases().size() > 1) {
            name = order.getPurchases().get(0).getGoodsName() + "...";
        } else {
            name = order.getPurchases().get(0).getGoodsName();
        }

        String token = iamportService.getToken();
        PaymentResponse response = iamportService.requestBilling(token, customerId, MERCHANT_BILLING_PREFIX + order.getId(), Long.toString(order.getPrice()), name);

        Payment payment = order.getPayment();
        if (response.getResponse().getStatus().equals("paid")) {
            payment.setMessage(response.getResponse().getStatus());
            payment.setReceipt(response.getResponse().getReceiptUrl());
            payment.setState(Payment.STATE_READY | Payment.STATE_PAID);
            if (StringUtils.isNotEmpty(response.getResponse().getCardName())) {
                payment.setCardName(response.getResponse().getCardName());
            }
            payment.setPaymentId(response.getResponse().getImpUid());
            paymentRepository.save(payment);

            completeOrder(order);
            slackService.sendForOrder(order);
        } else {
            String failReason = response.getResponse().getFailReason() == null ? "invalid payment price or state" : response.getResponse().getFailReason();
            log.warn("invalid_iamport_response, fail reason: {}", failReason);
            slackService.sendForImportPaymentMismatch(response.getResponse().getImpUid(), response.toString());
            payment.setState(Payment.STATE_FAILED);
            payment.setMessage(failReason);
            payment.setPaymentId(response.getResponse().getImpUid());
            paymentRepository.save(payment);
            throw new BadRequestException(response.getResponse().getFailReason());
        }

        return order;
    }

    @Transactional
    public OrderInquiry inquiryExchangeOrReturn(Order order, Byte state, String reason, Purchase purchase) {
        return inquiryExchangeOrReturn(order, state, reason, purchase, null);
    }

    @Transactional
    public OrderInquiry inquiryExchangeOrReturn(Order order, Byte state, String reason, Purchase purchase, String attachments) {
        if (purchase == null) {
            throw new BadRequestException("invalid purchase id");
        }

        String status;
        if (purchase.isDelivering() || purchase.isDelivered()) {
            switch (state) {
                case 1:
                    status = Order.Status.ORDER_EXCHANGING;
                    break;
                case 2:
                    status = Order.Status.ORDER_RETURNING;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown state type");
            }

            // TODO: send message order cancel message to slack?

            log.debug("status changed: {}", status);

            purchase.setStatus(status);
            purchaseRepository.save(purchase);

            OrderInquiry orderInquiry = new OrderInquiry(order, state, reason, purchase);
            if (!StringUtils.isBlank(attachments)) {
                orderInquiry.setAttachments(attachments);
            }
            return orderInquiryRepository.save(orderInquiry);
        } else {
            throw new BadRequestException("required purchase status delivered or delivering - " + purchase.getStatus());
        }
    }

    @Transactional
    public OrderInquiry cancelOrderInquire(Order order, Byte state, String reason) {
        if (order == null) {
            throw new BadRequestException("invalid purchase id");
        }

        if (Order.State.ORDER_CANCELLING.getValue() <= order.getState()) {
            throw new BadRequestException("Already requested order canceling");
        }

        if (Order.State.DELIVERING.getValue() <= order.getState()) {
            throw new BadRequestException("Invalid order status. need to paid or preparing");
        }

        order.setStatus(Order.Status.ORDER_CANCELLING);
        orderRepository.save(order);

        OrderInquiry orderInquiry = orderInquiryRepository.save(new OrderInquiry(order, state, reason));
        cancelPayment(order);
        return orderInquiry;
    }

    @Transactional
    public void cancelPayment(Order order) {
        if (order.getState() >= Order.State.ORDER_CANCELLED.getValue()) {
            throw new BadRequestException("invalid order status - " + order.getStatus());
        }

        Payment payment = paymentRepository.findById(order.getId()).orElseThrow(() -> new NotFoundException("invalid payment id"));

        if (order.getPrice() > 0) {
            String token = iamportService.getToken();
            iamportService.cancelPayment(token, payment.getPaymentId());
        }

        log.debug("cancel order: {}", order);
        saveOrderAndPurchasesStatus(order, Order.Status.ORDER_CANCELLED);

        payment.setState(Payment.STATE_CANCELLED);
        paymentRepository.save(payment);

        OrderInquiry orderInquiry = orderInquiryRepository.findByOrderAndCreatedBy(order, order.getCreatedBy()).orElseThrow(() -> new NotFoundException("invalid inquire id"));
        orderInquiry.setCompleted(true);
        orderInquiryRepository.save(orderInquiry);

        revokeResourcesBeforeCancelOrder(order);
    }

    @Transactional
    public Order notifyPayment(Order order, String status, String impUid) {
        log.info(String.format("notifyPayment called, id: %d, status: %s, impUid: %s ", order.getId(), status, impUid));
//    PaymentResponse response = iamportService.getPayment(iamportService.getToken(), impUid);
//
//    if (response.getCode() != 0 || response.getResponse() == null) {
//      log.warn("invalid_iamport_response, notifyPayment response is not success: " + response.getMessage());
//      slackService.sendForImportGetPaymentException(impUid, response.toString());
//      throw new PaymentConflictException();
//    }

        Payment payment = checkPaymentAndUpdate(order.getId(), impUid);

        if ((payment.getState() & Payment.STATE_CANCELLED) == Payment.STATE_CANCELLED) {
            order.setStatus(Order.Status.PAYMENT_CANCELLED);
            orderRepository.save(order);
        } else if ((payment.getState() & Payment.STATE_PAID) == Payment.STATE_PAID) {
            try {
                checkCoupon(order);
            } catch (MybeautipException e) {
                cancelOrderInquire(order, OrderInquiry.STATE_CANCEL_ORDER, "쿠폰 중복 사용으로 인한 취소");
            }

            completeOrder(order);
        } else if ((payment.getState() & Payment.STATE_FAILED) == Payment.STATE_FAILED) {
            order.setStatus(Order.Status.PAYMENT_FAILED);
            orderRepository.save(order);
        }
        return order;
    }

    private void checkCoupon(Order order) {
        if (order.getMemberCoupon() != null) {
            MemberCoupon memberCoupon = order.getMemberCoupon();
            if (memberCouponRepository.countByIdAndUsedAtIsNull(memberCoupon.getId()) == 0) {
                log.warn("{}", memberCoupon);

                slackService.sendUsedCouponUse(memberCoupon);
                throw new MybeautipException("Used coupon used Error");
            }
        }
    }

    @Transactional
    public Payment checkPaymentAndUpdate(Long orderId, String paymentId) {
        return paymentRepository.findById(orderId)
                .map(payment -> {
                    String token = iamportService.getToken();
                    PaymentResponse response = iamportService.getPayment(token, paymentId);
                    log.debug("response: {}", response);
                    int state = Payment.STATE_NOTIFIED;
                    if (response.getCode() == 0 || response.getResponse() != null) {
                        PaymentData iamportData = response.getResponse();
                        log.debug("paymentData: {}", iamportData.toString());
                        log.debug("payment amount: expected: {}, actual: {}", payment.getPrice(), iamportData.getAmount());
                        if ("paid".equals(iamportData.getStatus()) && iamportData.getAmount().equals(payment.getPrice())) {
                            payment.setMessage(iamportData.getStatus());
                            payment.setReceipt(iamportData.getReceiptUrl());
                            payment.setState(state | Payment.STATE_READY | Payment.STATE_PAID);
                            if (StringUtils.isNotEmpty(iamportData.getCardName())) {
                                payment.setCardName(iamportData.getCardName());
                            }
                        } else {
                            String failReason = iamportData.getFailReason() == null ? "invalid payment price or state" : response.getResponse().getFailReason();
                            log.warn("invalid_iamport_response, fail reason: {}", failReason);
                            slackService.sendForImportPaymentMismatch(paymentId, response.toString());
                            payment.setState(state | Payment.STATE_FAILED);
                            payment.setMessage(failReason);
                        }
                    } else {
                        slackService.sendForImportGetPaymentException(paymentId, response.toString());
                        payment.setMessage(response.getMessage());
                        payment.setPaymentId(paymentId);
                        payment.setState(state | Payment.STATE_STOPPED);
                    }

                    payment.setPaymentId(paymentId);
                    return paymentRepository.save(payment);
                })
                .orElseThrow(() -> new NotFoundException("invalid order id or payment id"));
    }

    /**
     * Update order status and relative purchases of order
     */
    @Transactional
    public void saveOrderAndPurchasesStatus(Order order, String status) {
        log.info(String.format("saveOrderAndPurchasesStatus called, id: %d, status: %s", order.getId(), status));
        order.setStatus(status);
        order.getPurchases().forEach(p -> p.setStatus(status));

        orderRepository.save(order);
        log.debug("order: {}", order);
    }

    @Transactional
    public void completeOrder(Order order) {
        log.info(String.format("completeOrder called, id: %d, state: %s ", order.getId(), order.getStatus()));
        saveOrderAndPurchasesStatus(order, Order.Status.PAID);

        log.info(String.format("completeOrder point: %d", order.getPoint()));
        if (order.getPoint() >= minimumPoint) {
            Member member = order.getCreatedBy();
            member.setPoint(member.getPoint() - order.getPoint());
            memberRepository.save(member);

            memberPointService.usePoints(order, order.getPoint());
        }

        if (order.getMemberCoupon() == null) {
            memberPointService.earnPoints(order);
        } else {
            MemberCoupon memberCoupon = order.getMemberCoupon();
            if (!memberCoupon.getCoupon().getId().equals(EVENT_COUPON_ID)) {
                memberCoupon.setUsedAt(new Date());
            }
            memberCouponRepository.save(memberCoupon);
        }

        if (order.getVideoId() != null) {
            if (isOrderOnLive(order)) {
                log.info("Order on Live - order_id: {}, video_id: {}", order.getId(), order.getVideoId());
                saveRevenuesForSeller(order, "LIVE");
            } else {
                log.info("Order on VOD - order_id: {}, video_id: {}", order.getId(), order.getVideoId());
                saveRevenuesForSeller(order, "VOD");
            }

            videoRepository.updateOrderCount(order.getVideoId(), 1);
        }

        deleteCartItems(order);

        // TODO: Notify ?
        // TODO: Send email ?
    }

    @Transactional
    public boolean isOrderOnLive(Order order) {
        if (order.getVideoId() == null) {
            return false;
        }

        if (order.getOnLive() == null) {
            Video video = videoRepository.findById(order.getVideoId()).orElse(null);
            if (video == null) {
                log.warn("Something wrong! Not found video: " + order.getVideoId());
                return false;
            }

            if (!"BROADCASTED".equals(video.getType())) { // only broadcasted type video order grant revenue
                return false;
            }

            if ("LIVE".equals(video.getState()) || video.getEndedAt() == null) {
                // FIXME: Remove when on_live in request is mandatory
                order.setOnLive(true);
                orderRepository.save(order);
                for (Purchase purchase : order.getPurchases()) {
                    purchase.setOnLive(true);
                }

                return true;
            } else {  // VOD
                if (video.getEndedAt() == null) {
                    log.warn("Something wrong! Video state is not LIVE, but endedAt is null: " + video.getId());
                    return false;
                }

                boolean isOnLive = order.getCreatedAt().getTime() <= (video.getEndedAt().getTime() + REVENUE_DURATION_AFTER_LIVE_ENDED);
                // FIXME: Remove when on_live in request is mandatory
                order.setOnLive(true);
                orderRepository.save(order);
                for (Purchase purchase : order.getPurchases()) {
                    purchase.setOnLive(true);
                }

                return isOnLive;
            }
        } else {
            return order.getOnLive();
        }
    }

    /**
     * Save revenues for seller
     */
    @Transactional
    public void saveRevenuesForSeller(Order order, String videoState) {
        Map<Goods, Video> videoGoods = videoGoodsRepository.findAllByVideoId(order.getVideoId())
                .stream().collect(Collectors.toMap(VideoGoods::getGoods, VideoGoods::getVideo));

        log.debug("video goods: {}", videoGoods);
        order.getPurchases().forEach(
                p -> {
                    if (videoGoods.containsKey(p.getGoods())) {
                        int revenueAmount = 0;
                        if ("LIVE".equals(videoState)) {
                            revenueAmount = revenueService.getRevenueForLive(p.getTotalPrice());
                        }
                        if ("VOD".equals(videoState)) {
                            revenueAmount = revenueService.getRevenueForVOD(p.getTotalPrice());
                        }
                        revenueService.save(videoGoods.get(p.getGoods()), p, revenueAmount);
                    }
                }
        );
    }

    private String orderNumber() {
        return df.format(new Date()) + new Random().nextInt(10);
    }

    // Delete cart items when order is completed(paid)
    @Transactional
    public void deleteCartItems(Order order) {
        log.debug("delete cart items: purchase count is " + order.getPurchases().size());
        for (Purchase p : order.getPurchases()) {
            log.debug(String.format("- item: %s, %d, %d", p.getGoods().getGoodsNo(), p.getOptionId().intValue(), p.getQuantity()));

            if (p.getOptionId() == 0) {
                cartRepository.findByGoodsGoodsNoAndOptionIsNullAndCreatedById(
                                p.getGoods().getGoodsNo(), order.getCreatedBy().getId())
                        .ifPresent(cartRepository::delete);
            } else {
                cartRepository.findByGoodsGoodsNoAndOptionOptionNoAndCreatedById(
                                p.getGoods().getGoodsNo(), p.getOptionId().intValue(), order.getCreatedBy().getId())
                        .ifPresent(cartRepository::delete);
            }
        }
    }

    public Long parseOrderId(String merchantId) throws NumberFormatException {
        return Long.parseLong(StringUtils.substringAfter(merchantId, MERCHANT_PREFIX));
    }

    @Transactional
    public Purchase confirmPurchase(Purchase purchase) {
        log.debug("purchase confirmed: {}", purchase.getId());

        // Confirm revenue and Append monthly revenue estimatedAmount if revenue exist
        revenueRepository.findByPurchaseId(purchase.getId())
                .ifPresent(revenue -> {
                    RevenuePayment revenuePayment = revenue.getRevenuePayment();
                    if (revenuePayment == null) {
                        throw new NotFoundException("Revenue payment is null");
                    }
                    revenuePaymentService.appendEstimatedAmount(revenuePayment, revenue.getRevenue());

                    memberRepository.findByIdAndDeletedAtIsNull(revenue.getVideo().getMember().getId())
                            .ifPresent(member -> {
                                member.setRevenueModifiedAt(new Date());
                                memberRepository.save(member);
                            });

                    revenue.setConfirmed(true);
                    revenueRepository.save(revenue);
                });

        // Update purchase state
        purchase.setState(Order.State.CONFIRMED);
        purchase.setStatus(Order.State.CONFIRMED.name());
        return purchaseRepository.save(purchase);
    }

    @Transactional
    public OrderInquiry cancelOrderInquireByAdmin(Order order, Payment payment, Byte state, String reason) {
        if (order.getState() != Order.State.PAID.getValue()) {
            throw new BadRequestException("Invalid order state: " + order.getState());
        }

        log.debug("cancel order: {}", order);

        // 1. Prepare order cancel
        order.setStatus(Order.Status.ORDER_CANCELLED);
        orderRepository.save(order);

        // 2. Create order cancel inquiry
        OrderInquiry orderInquiry = new OrderInquiry(order, state, reason);
        orderInquiry = orderInquiryRepository.save(orderInquiry);

        // 3. Cancel order and payment without Iamport
        saveOrderAndPurchasesStatus(order, Order.Status.ORDER_CANCELLED);
        payment.setState(Payment.STATE_CANCELLED);
        payment.setMessage(reason);
        paymentRepository.save(payment);

        // 4. Complete order cancel inquiry
        orderInquiry.setCompleted(true);
        orderInquiry.setCreatedBy(order.getCreatedBy());
        orderInquiryRepository.save(orderInquiry);

        revokeResourcesBeforeCancelOrder(order);
        return orderInquiry;
    }

    @Transactional
    public OrderInquiry cancelPaymentByAdmin(Order order, Payment payment, Byte state, String reason) {
        if (order.getState() != Order.State.DELIVERED.getValue()) {
            throw new BadRequestException("Invalid order state: " + order.getState());
        }

        log.debug("cancel order: {}", order);
        String orderStatus = Order.Status.ORDER_CANCELLED;

        // Cancel payment without Iamport
        saveOrderAndPurchasesStatus(order, orderStatus);
        payment.setState(Payment.STATE_CANCELLED);
        payment.setMessage(reason);
        paymentRepository.save(payment);

        if (order.getPrice() > 0) {
            String token = iamportService.getToken();
            iamportService.cancelPayment(token, payment.getPaymentId());
        }

        OrderInquiry orderInquiry = orderInquiryRepository.findByOrderAndCreatedBy(order, order.getCreatedBy())
                .orElseThrow(() -> new NotFoundException("inquire_not_found"));
        orderInquiry.setComment("canceled_by_admin");
        orderInquiryRepository.save(orderInquiry);

        revokeResourcesBeforeCancelOrder(order);
        return orderInquiry;
    }

    @Transactional
    public Order confirmOrder(Order order) {
        // Convert earning point state
        memberPointRepository.findByMemberAndOrderAndPointAndState(
                        order.getCreatedBy(), order, order.getExpectedPoint(), MemberPoint.STATE_WILL_BE_EARNED)
                .ifPresent(memberPointService::convertPoint);

        // Update order state
        order.setState(Order.State.CONFIRMED);
        order.setStatus(Order.State.CONFIRMED.name());

        return orderRepository.save(order);
    }

    @Transactional
    public Order confirmOrderAndPurchase(Order order) {
        for (Purchase purchase : order.getPurchases()) {
            confirmPurchase(purchase);
        }
        return confirmOrder(order);
    }

    @Transactional
    public void revokeResourcesBeforeCancelOrder(Order order) {
        // Revoke used coupon
        if (order.getMemberCoupon() != null) {
            log.info("Order canceled - coupon revoked: {}, {}", order.getId(), order.getMemberCoupon());
            MemberCoupon memberCoupon = order.getMemberCoupon();
            memberCoupon.setUsedAt(null);
            memberCouponRepository.save(memberCoupon);
        }

        memberPointService.revokePoints(order);

        // Revoke video order count & revenues
        if (order.getVideoId() != null) {
            videoRepository.findById(order.getVideoId())
                    .ifPresent(video -> {
                        // Update video order count
                        if (video.getOrderCount() > 0) {
                            videoRepository.updateOrderCount(video.getId(), -1);
                        }

                        // Remove revenues
                        log.info("Order canceled - revenue per purchase removed: {}, {}", order.getId(), order.getMemberCoupon());
                        for (Purchase purchase : order.getPurchases()) {
                            revenueRepository.findByPurchaseId(purchase.getId())
                                    .ifPresent(revenueService::remove);
                        }
                    });
        }
    }

    @Transactional(readOnly = true)
    public Map<Long, String> getPurchaseNameMap(Set<Long> orderIds) {
        Map<Long, String> result = new HashMap<>();
        List<Order> orderList = orderRepository.findByIdIn(orderIds);

        for (Order order : orderList) {
            String goodsName = order.getPurchases().stream()
                    .findFirst()
                    .map(Purchase::getGoodsName)
                    .orElse("");

            result.put(order.getId(), goodsName);
        }

        return result;
    }

}
