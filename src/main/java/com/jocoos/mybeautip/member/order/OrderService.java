package com.jocoos.mybeautip.member.order;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsRepository;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.cart.CartRepository;
import com.jocoos.mybeautip.member.coupon.MemberCoupon;
import com.jocoos.mybeautip.member.coupon.MemberCouponRepository;
import com.jocoos.mybeautip.member.point.PointService;
import com.jocoos.mybeautip.member.revenue.RevenueService;
import com.jocoos.mybeautip.restapi.OrderController;
import com.jocoos.mybeautip.support.payment.IamportService;
import com.jocoos.mybeautip.support.payment.PaymentResponse;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoGoods;
import com.jocoos.mybeautip.video.VideoGoodsRepository;

@Slf4j
@Service
public class OrderService {

  @Value("${mybeautip.point.minimum}")
  private int minimumPoint;

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
  private final RevenueService revenueService;
  private final PointService pointService;
  private final IamportService iamportService;

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
                      RevenueService revenueService,
                      PointService pointService,
                      IamportService iamportService) {
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
    this.revenueService = revenueService;
    this.pointService = pointService;
    this.iamportService = iamportService;
  }

  @Transactional
  public Order create(OrderController.CreateOrderRequest request, Member member) {
    if (member == null) {
      throw new MemberNotFoundException("Login required");
    }

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
        throw new BadRequestException("invalid_point", "minimum point - " + minimumPoint);
      }

      if (member.getPoint() < request.getPoint()) {
        throw new BadRequestException("invalid_point", "member point not enough");
      }
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
    request.getPurchases().forEach(p -> {
      goodsRepository.findByGoodsNo(p.getGoodsNo())
        .map(goods -> {
          Purchase purchase = new Purchase(order.getId(), goods);
          BeanUtils.copyProperties(p, purchase);

          purchase.setTotalPrice(Long.valueOf(p.getQuantity() * p.getGoodsPrice()));
          purchases.add(purchase);
          return Optional.empty();
        })
        .orElseThrow(() -> new NotFoundException("goods_not_found", "Goods not found:" + p.getGoodsNo()));
    });

    log.debug("purchases: {}", purchases);
    order.setPurchases(purchases);
    orderRepository.save(order);

    return order;
  }

  @Transactional
  public String complete(String uid, Long id, boolean isSuccess, String message) {
    return orderRepository.findByIdAndDeletedAtIsNull(id)
        .map(order -> {
          if (!isSuccess) {
            updatePaymentState(order.getId(), uid, Payment.STATE_STOPPED, message);
            return getErrorHtml(id, message);
          }

          long memberId = order.getCreatedBy().getId();
          // Delete cart item when order is completed
          log.debug("order complete: purchase count is " + order.getPurchases().size());
          for (Purchase p: order.getPurchases()) {
            log.debug(String.format("purchase::%s, option: %d, quantity: %d", p.getGoods().getGoodsNo(), p.getOptionId().intValue(), p.getQuantity()));
            
            // delete purchases from cart when order completed
            if (p.getOptionId() == 0) {
              cartRepository.findByGoodsGoodsNoAndOptionIsNullAndCreatedById(
                  p.getGoods().getGoodsNo(), memberId)
                  .ifPresent(cartRepository::delete);
            } else {
              cartRepository.findByGoodsGoodsNoAndOptionOptionNoAndCreatedById(
                  p.getGoods().getGoodsNo(), p.getOptionId().intValue(), memberId)
                  .ifPresent(cartRepository::delete);
            }
          }

          Payment payment = checkPaymentAndUpdate(order.getId(), uid);
          if ((payment.getState() & Payment.STATE_PAID) != 0) {
            if (Order.State.PAID.getValue() != order.getState()) {
              completeOrder(order);
            }
          } else {
            return getErrorHtml(id, message);
          }
          return getSuccessHtml(id);
       }).orElseThrow(() -> new NotFoundException("order_not_found", "invalid order id"));
  }

  @Transactional
  public OrderInquiry inquiryExchangeOrReturn(Order order, Byte state, String reason, Purchase purchase) {
    if (purchase == null) {
      throw new BadRequestException("purchase_not_found", "invalid purchase id");
    }

    String status = purchase.getStatus();
    if (purchase.isDevlivering() || purchase.isDevlivered()) {
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
    if (order.getState() >= Order.State.ORDER_CANCELLING.getValue()) {
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
  }

  @Transactional
  public Order notifyPayment(Order order, String status, String impUid) {
    int state = 0;
    if (!Strings.isNullOrEmpty(status)) {
      state = stateValue(status);
    }

    state = state | Payment.STATE_NOTIFIED;
    updatePaymentState(order.getId(), impUid, state, status);

    if ((state & Payment.STATE_CANCELLED) != 0) {
      order.setStatus(Order.Status.PAYMENT_CANCELLED);
      orderRepository.save(order);
    } else if ((state & Payment.STATE_PAID) != 0) {
      completeOrder(order);
    } else if ((state & Payment.STATE_FAILED) != 0) {
      order.setStatus(Order.Status.PAYMENT_FAILED);
      orderRepository.save(order);
    }
    return order;
  }

  private Payment checkPaymentAndUpdate(Long orderId, String paymentId) {
    return paymentRepository.findById(orderId)
       .map(payment -> {
         int state = 0;
         String token = iamportService.getToken();
         log.debug("access token: {}", token);
         PaymentResponse response = iamportService.getPayment(token, paymentId);
         log.debug("response: {}", response);
         if (response.getCode() == 0) {
           state = stateValue(response.getResponse().getStatus());
           log.debug("state: {}", state);
           log.debug("response amount: %d, payment price: %d", response.getResponse().getAmount(), payment.getPrice());
           if (state == Payment.STATE_PAID && response.getResponse().getAmount().equals(payment.getPrice())) {
             payment.setMessage(response.getResponse().getStatus());
             payment.setReceipt(response.getResponse().getReceiptUrl());
             payment.setState(state | Payment.STATE_PURCHASED);
           } else {
             String failReason = response.getResponse().getFailReason() == null ? "invalid payment price or state" : response.getResponse().getFailReason();
             log.warn("fail reason: {}", failReason);
             payment.setState(Payment.STATE_FAILED);
             payment.setMessage(failReason);
           }
         } else {
           payment.setMessage(response.getMessage());
         }

         payment.setPaymentId(paymentId);
         return paymentRepository.save(payment);
       })
    .orElseThrow(() -> new NotFoundException("payment_not_found", "invalid order id or payment id"));
  }

  private void saveOrderAndPurchasesStatus(Order order, String status) {
    order.setStatus(status);
    order.getPurchases().stream().forEach(p -> {
      p.setStatus(status);
    });

    orderRepository.save(order);
    log.debug("order: {}", order);
  }

  private int stateValue(String state) {
    switch (state) {
      case "paid": {
        return Payment.STATE_PAID;
      }
      case "cancelled": {
        return Payment.STATE_CANCELLED;
      }
      case "failed": {
        return Payment.STATE_FAILED;
      }
      default:
        throw new IllegalArgumentException("Unknown state type");
    }
  }

  @Transactional
  private Payment updatePaymentState(Long id, String paymentId, int state, String message) {
    return paymentRepository.findById(id)
       .map(payment -> {
         payment.setPaymentId(paymentId);
         payment.setState(state);
         if (message != null) {
           payment.setMessage(message);
         }
         return paymentRepository.save(payment);
       })
       .orElseThrow(() -> new NotFoundException("payment_not_found", "invalid payment id"));
  }

//  @Transactional
  private void completeOrder(Order order) {
    log.debug(String.format("completeOrder called, id: %d, state: %s ", order.getId(), order.getStatus()));
    saveOrderAndPurchasesStatus(order, Order.Status.PAID);

    log.debug("completeOrder point: %d" + order.getPoint());
    if (order.getPoint() >= minimumPoint) {
      Member member = order.getCreatedBy();
      member.setPoint(member.getPoint() - order.getPoint());
      memberRepository.save(member);

      pointService.usePoints(member, order.getPoint());
    }

    if (order.getMemberCoupon() == null) {
      pointService.earnPoints(order.getCreatedBy(), order.getPrice());
    }

    if (order.getVideoId() != null) {
      saveRevenuesForSeller(order);
    }

    // TODO: Notify ?
    // TODO: Send email ?
    // TODO: Send To Slack Message?
  }

  /**
   * Save revenues for seller
   */
  private void saveRevenuesForSeller(Order order) {
    Map<Goods, Video> videoGoods = videoGoodsRepository.findAllByVideoId(order.getVideoId())
       .stream().collect(Collectors.toMap(VideoGoods::getGoods, VideoGoods::getVideo));

    log.debug("video goods: {}", videoGoods);
    order.getPurchases().forEach(
       p -> {
         if (videoGoods.containsKey(p.getGoods())) {
           revenueService.save(videoGoods.get(p.getGoods()), p);
           memberRepository.findByIdAndDeletedAtIsNull(order.getCreatedBy().getId())
               .ifPresent(member -> {
                 member.setRevenueModifiedAt(new Date());
                 memberRepository.save(member);
               });
         }
       }
    );
  }

  private String getSuccessHtml(Long id) {
    return new StringBuilder("<script language=\"javascript\">")
       .append("mybeautip.success(" + id + ");")
       .append("</script>").toString();
  }

  private String getErrorHtml(Long id, String message) {
    return new StringBuilder("<script language=\"javascript\">")
       .append("mybeautip.fail(" + id + ", \"" + message + "\");")
       .append("</script>").toString();
  }

  private String orderNumber() {
    return df.format(new Date()) + new Random().nextInt(10);
  }
}
