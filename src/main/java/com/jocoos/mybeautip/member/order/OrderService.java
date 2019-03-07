package com.jocoos.mybeautip.member.order;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.exception.PaymentConflictException;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.cart.CartRepository;
import com.jocoos.mybeautip.member.coupon.MemberCoupon;
import com.jocoos.mybeautip.member.coupon.MemberCouponRepository;
import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.member.point.MemberPointRepository;
import com.jocoos.mybeautip.member.point.PointService;
import com.jocoos.mybeautip.member.revenue.RevenuePayment;
import com.jocoos.mybeautip.member.revenue.RevenuePaymentService;
import com.jocoos.mybeautip.member.revenue.RevenueRepository;
import com.jocoos.mybeautip.member.revenue.RevenueService;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.notification.NotificationService;
import com.jocoos.mybeautip.restapi.OrderController;
import com.jocoos.mybeautip.support.payment.IamportService;
import com.jocoos.mybeautip.support.payment.PaymentData;
import com.jocoos.mybeautip.support.payment.PaymentResponse;
import com.jocoos.mybeautip.support.slack.SlackService;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoGoods;
import com.jocoos.mybeautip.video.VideoGoodsRepository;
import com.jocoos.mybeautip.video.VideoRepository;
import com.jocoos.mybeautip.video.VideoService;

@Slf4j
@Service
public class OrderService {

  @Value("${mybeautip.point.minimum}")
  private int minimumPoint;
  
  private static final String GOODS_NOT_FOUND = "goods.not_found";
  private static final String POINT_BAD_REQUEST = "order.point_bad_rqeust";
  private static final String POINT_NOT_ENOUGH = "order.point_not_enough";
  private static final String POINT_BAD_REQUEST_MIN_PRICE_CONDITION= "order.price_not_enough_to_use_point";
  private static final int MIN_PRICE_TO_USE_POINT= 30000;
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
  private final PointService pointService;
  private final IamportService iamportService;
  private final MessageService messageService;
  private final VideoService videoService;
  private final SlackService slackService;
  private final NotificationService notificationService;

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
                      PointService pointService,
                      IamportService iamportService,
                      MessageService messageService,
                      VideoService videoService,
                      SlackService slackService,
                      NotificationService notificationService) {
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
    this.pointService = pointService;
    this.iamportService = iamportService;
    this.messageService = messageService;
    this.videoService = videoService;
    this.slackService = slackService;
    this.notificationService = notificationService;
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
        throw new BadRequestException("point_bad_request", messageService.getMessage(POINT_BAD_REQUEST, lang));
      }

      if (member.getPoint() < request.getPoint()) {
        throw new BadRequestException("point_not_enough", messageService.getMessage(POINT_NOT_ENOUGH, lang));
      }
      
      int price = request.getPriceAmount() + request.getShippingAmount() - request.getDeductionAmount();
      if (price < MIN_PRICE_TO_USE_POINT) {
        throw new BadRequestException("point_bad_request",
            messageService.getMessage(POINT_BAD_REQUEST_MIN_PRICE_CONDITION, lang));
      }
    }
    
    // Modify member phoneNumber with Order buyerPhoneNumber
    if (!member.getPhoneNumber().equals(request.getBuyerPhoneNumber())) {
      member.setPhoneNumber(request.getBuyerPhoneNumber());
      memberRepository.save(member);
    }

    if (request.getCouponId() != null) {
      MemberCoupon memberCoupon = memberCouponRepository.findById(request.getCouponId()).orElseThrow(() -> new BadRequestException("coupon_not_found", "invalid member coupon id"));
      order.setMemberCoupon(memberCoupon);
      memberCoupon.setUsedAt(new Date());
      memberCouponRepository.save(memberCoupon);
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


    List<Purchase> purchases = Lists.newArrayList();
    request.getPurchases().forEach(p -> goodsRepository.findByGoodsNo(p.getGoodsNo())
      .map(goods -> {
        Purchase purchase = new Purchase(order.getId(), goods);
        BeanUtils.copyProperties(p, purchase);
        
        purchase.setVideoId(order.getVideoId());
        purchase.setTotalPrice((long) (p.getQuantity() * p.getGoodsPrice()));
        purchases.add(purchase);
        return Optional.empty();
      })
      .orElseThrow(() -> new NotFoundException("goods_not_found", messageService.getMessage(GOODS_NOT_FOUND, lang))));

    log.debug("purchases: {}", purchases);
    order.setPurchases(purchases);
    orderRepository.save(order);

    return order;
  }
  
  @Transactional
  public OrderInquiry inquiryExchangeOrReturn(Order order, Byte state, String reason, Purchase purchase) {
    if (purchase == null) {
      throw new BadRequestException("purchase_not_found", "invalid purchase id");
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

      return orderInquiryRepository.save(new OrderInquiry(order, state, reason, purchase));
    } else {
      throw new BadRequestException("required purchase status delivered or delivering - " + purchase.getStatus());
    }
  }

  @Transactional
  public OrderInquiry cancelOrderInquire(Order order, Byte state, String reason) {
    if (order == null) {
      throw new BadRequestException("purchase_not_found", "invalid purchase id");
    }

    if (Order.State.ORDER_CANCELLING.getValue() <= order.getState()) {
      throw new BadRequestException("order_cancel_duplicated", "Already requested order canceling");
    }

    if (Order.State.DELIVERING.getValue() <= order.getState()) {
      throw new BadRequestException("order_cancel_failed", "Invalid order status. need to paid or preparing");
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
      throw new BadRequestException("invalid_order_status", "invalid order status - " + order.getStatus());
    }

    Payment payment = paymentRepository.findById(order.getId()).orElseThrow(() -> new NotFoundException("payment_not_found", "invalid payment id"));

    String token = iamportService.getToken();
    iamportService.cancelPayment(token, payment.getPaymentId());

    log.debug("cancel order: {}", order);
    saveOrderAndPurchasesStatus(order, Order.Status.ORDER_CANCELLED);

    payment.setState(Payment.STATE_CANCELLED);
    paymentRepository.save(payment);

    OrderInquiry orderInquiry = orderInquiryRepository.findByOrderAndCreatedBy(order, order.getCreatedBy()).orElseThrow(() -> new NotFoundException("inquire_not_found", "invalid inquire id"));
    orderInquiry.setCompleted(true);
    orderInquiryRepository.save(orderInquiry);
  
    revokeResourceWhenOrderCanceled(order);
  }

  @Transactional
  public Order notifyPayment(Order order, String status, String impUid) {
    log.info(String.format("notifyPayment called, id: %d, status: %s, impUid: %s ", order.getId(), status, impUid));
    PaymentResponse response = iamportService.getPayment(iamportService.getToken(), impUid);
    
    if (response.getCode() != 0 || response.getResponse() == null) {
      log.warn("invalid_iamport_response, notifyPayment response is not success: " + response.getMessage());
      slackService.sendForImportGetPaymentException(impUid, response.toString());
      throw new PaymentConflictException();
    }
    
    Payment payment = checkPaymentAndUpdate(order.getId(), impUid);
    
    if ((payment.getState() & Payment.STATE_CANCELLED) == Payment.STATE_CANCELLED) {
      order.setStatus(Order.Status.PAYMENT_CANCELLED);
      orderRepository.save(order);
    } else if ((payment.getState() & Payment.STATE_PAID) == Payment.STATE_PAID) {
      completeOrder(order);
    } else if ((payment.getState() & Payment.STATE_FAILED) == Payment.STATE_FAILED) {
      order.setStatus(Order.Status.PAYMENT_FAILED);
      orderRepository.save(order);
    }
    return order;
  }

  @Transactional
  private Payment checkPaymentAndUpdate(Long orderId, String paymentId) {
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
             log.warn("invalid_iamport_response, fail reason: ", failReason);
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
    .orElseThrow(() -> new NotFoundException("payment_not_found", "invalid order id or payment id"));
  }
  
  /**
   * Update order status and relative purchases of order
   */
  @Transactional
  private void saveOrderAndPurchasesStatus(Order order, String status) {
    log.info(String.format("saveOrderAndPurchasesStatus called, id: %d, status: %s", order.getId(), status));
    order.setStatus(status);
    order.getPurchases().forEach(p -> p.setStatus(status));

    orderRepository.save(order);
    log.debug("order: {}", order);
  }
  
  @Transactional
  private void completeOrder(Order order) {
    log.info(String.format("completeOrder called, id: %d, state: %s ", order.getId(), order.getStatus()));
    saveOrderAndPurchasesStatus(order, Order.Status.PAID);

    log.info(String.format("completeOrder point: %d", order.getPoint()));
    if (order.getPoint() >= minimumPoint) {
      Member member = order.getCreatedBy();
      member.setPoint(member.getPoint() - order.getPoint());
      memberRepository.save(member);

      pointService.usePoints(order, order.getPoint());
    }

    if (order.getMemberCoupon() == null) {
      pointService.earnPoints(order);
    }

    if (order.getVideoId() != null) {
      saveRevenuesForSeller(order);
      videoRepository.updateOrderCount(order.getVideoId(), 1);
      notificationService.notifyOrder(order, order.getVideoId());
    }
    
    deleteCartItems(order);

    // TODO: Notify ?
    // TODO: Send email ?
  }

  /**
   * Save revenues for seller
   */
  @Transactional
  private void saveRevenuesForSeller(Order order) {
    Map<Goods, Video> videoGoods = videoGoodsRepository.findAllByVideoId(order.getVideoId())
       .stream().collect(Collectors.toMap(VideoGoods::getGoods, VideoGoods::getVideo));

    log.debug("video goods: {}", videoGoods);
    order.getPurchases().forEach(
       p -> {
         if (videoGoods.containsKey(p.getGoods())) {
           revenueService.save(videoGoods.get(p.getGoods()), p);
         }
       }
    );
  }

  private String orderNumber() {
    return df.format(new Date()) + new Random().nextInt(10);
  }
  
  // Delete cart items when order is completed(paid)
  @Transactional
  private void deleteCartItems(Order order) {
    log.debug("delete cart items: purchase count is " + order.getPurchases().size());
    for (Purchase p: order.getPurchases()) {
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
            throw new NotFoundException("revenue_payment_not_found", "Revenue payment is null");
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
    purchase.setConfirmed(true);  // FIXME: duplicate with state 'CONFIRMED', should be deprecated...?
    return purchaseRepository.save(purchase);
  }
  
  @Transactional
  public OrderInquiry cancelOrderInquireByAdmin(Order order, Payment payment, Byte state, String reason) {
    if (order.getState() != Order.State.PAID.getValue()) {
      throw new BadRequestException("invalid_order_state", "Invalid order state: " + order.getState());
    }
    
    log.debug("cancel order: {}", order);
    
    // 1. Prepare order cancel
    order.setStatus(Order.Status.ORDER_CANCELLING);
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
  
    revokeResourceWhenOrderCanceled(order);
    return orderInquiry;
  }
  
  @Transactional
  public Order confirmOrder(Order order) {
    // Convert earning point state
    memberPointRepository.findByMemberAndOrderAndPointAndState(
        order.getCreatedBy(), order, order.getExpectedPoint(), MemberPoint.STATE_WILL_BE_EARNED)
        .ifPresent(pointService::convertPoint);
  
    // Update order state
    order.setState(Order.State.CONFIRMED);
    order.setStatus(Order.State.CONFIRMED.name());
    
    return orderRepository.save(order);
  }
  
  @Transactional
  public void revokeResourceWhenOrderCanceled(Order order) {
    // Revoke used coupon
    if (order.getMemberCoupon() != null) {
      log.info("Order canceled - coupon revoked: {}, {}", order.getId(), order.getMemberCoupon());
      MemberCoupon memberCoupon = order.getMemberCoupon();
      memberCoupon.setUsedAt(null);
      memberCouponRepository.save(memberCoupon);
    }
  
    // Revoke used point
    if (order.getPoint() > 0) {
      log.info("Order canceled - used point revoked: {}, {}", order.getId(), order.getPoint());
      Member member = order.getCreatedBy();
      member.setPoint(member.getPoint() + order.getPoint());
      memberRepository.save(member);
    
      memberPointRepository.findByMemberAndOrderAndPointAndState(
          order.getCreatedBy(), order, order.getPoint(), MemberPoint.STATE_USE_POINT)
          .ifPresent(memberPointRepository::delete);
    }
  
    // Remove expected earning point
    if (order.getExpectedPoint() > 0) {
      log.info("Order canceled - expected earning point removed: {}, {}", order.getId(), order.getExpectedPoint());
      memberPointRepository.findByMemberAndOrderAndPointAndState(
          order.getCreatedBy(), order, order.getExpectedPoint(), MemberPoint.STATE_WILL_BE_EARNED)
          .ifPresent(memberPointRepository::delete);
    }
  
    // Revoke video order count & revenues
    if (order.getVideoId() != null) {
      videoRepository.findById(order.getVideoId())
          .ifPresent(video -> {
            // Update video order count
            videoRepository.updateOrderCount(order.getVideoId(), -1);
          
            // Remove revenues
            log.info("Order canceled - revenue per purchase removed: {}, {}", order.getId(), order.getMemberCoupon());
            for (Purchase purchase : order.getPurchases()) {
              revenueRepository.findByPurchaseId(purchase.getId())
                  .ifPresent(revenueService::remove);
            }
          });
    }
  }
}
