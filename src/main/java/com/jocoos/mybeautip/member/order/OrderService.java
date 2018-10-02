package com.jocoos.mybeautip.member.order;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.coupon.MemberCoupon;
import com.jocoos.mybeautip.member.coupon.MemberCouponRepository;
import com.jocoos.mybeautip.member.point.PointService;
import com.jocoos.mybeautip.member.revenue.Revenue;
import com.jocoos.mybeautip.member.revenue.RevenueService;
import com.jocoos.mybeautip.restapi.OrderController;
import com.jocoos.mybeautip.support.payment.IamportService;
import com.jocoos.mybeautip.support.payment.PaymentResponse;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoGoods;
import com.jocoos.mybeautip.video.VideoGoodsRepository;
import com.jocoos.mybeautip.video.VideoRepository;

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
  private final OrderInquiryRepository orderInquiryRepository;
  private final MemberCouponRepository memberCouponRepository;
  private final VideoRepository videoRepository;
  private final VideoGoodsRepository videoGoodsRepository;
  private final RevenueService revenueService;
  private final PointService pointService;
  private final IamportService iamportService;

  public OrderService(OrderRepository orderRepository,
                      MemberRepository memberRepository,
                      DeliveryRepository deliveryRepository,
                      PaymentRepository paymentRepository,
                      OrderInquiryRepository orderInquiryRepository,
                      MemberCouponRepository memberCouponRepository,
                      VideoRepository videoRepository,
                      VideoGoodsRepository videoGoodsRepository,
                      RevenueService revenueService, PointService pointService,
                      IamportService iamportService) {
    this.orderRepository = orderRepository;
    this.memberRepository = memberRepository;
    this.deliveryRepository = deliveryRepository;
    this.paymentRepository = paymentRepository;
    this.orderInquiryRepository = orderInquiryRepository;
    this.memberCouponRepository = memberCouponRepository;
    this.videoRepository = videoRepository;
    this.videoGoodsRepository = videoGoodsRepository;
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
    BeanUtils.copyProperties(request, order);
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
      Purchase purchase = new Purchase(order.getId(), Order.ORDER);
      BeanUtils.copyProperties(p, purchase);

      purchase.setTotalPrice(Long.valueOf(p.getQuantity() * p.getGoodsPrice()));
      purchases.add(purchase);
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

          Payment payment = checkPaymentAndUpdate(order.getId(), uid);
          if ((payment.getState() & Payment.STATE_PAID) != 0) {
            if (Order.PAID != order.getStatus()) {
              completeOrder(order);
            }
          } else {
            return getErrorHtml(id, message);
          }
          return getSuccessHtml(id);
       }).orElseThrow(() -> new NotFoundException("order_not_found", "invalid order id"));
  }

  @Transactional
  public OrderInquiry inquireOrder(Order order, Byte state, String reason, Purchase purchase) {
    if (Order.ORDER_CANCELLED.equals(order.getStatus()) || Order.ORDER_CANCELLING.equals(order.getStatus())) {
      throw new BadRequestException("order_cancel_duplicated", "Already requested order canceling");
    }

    String status = null;
    switch (state) {
      case 0: {
        if (Order.PAID.equals(order.getStatus()) || Order.PREPARING.equals(order.getStatus())) {
          status = Order.ORDER_CANCELLING;
        } else {
          throw new BadRequestException("Can not cancel payment - " + order.getStatus());
        }
        break;
      }
      case 1: {
        if (Order.DELIVERED.equals(order.getStatus()) || Order.DELIVERING.equals(order.getStatus())) {
          status = Order.ORDER_EXCHANGING;
        } else {
          throw new BadRequestException("invalid order exchange inquire - " + order.getStatus());
        }
        break;
      }
      case 2: {
        if (Order.DELIVERED.equals(order.getStatus()) || Order.DELIVERING.equals(order.getStatus())) {
          status = Order.ORDER_RETURNING;
        } else {
          throw new BadRequestException("invalid order return inquire - " + order.getStatus());
        }
        break;
      }
      default: {
        throw new IllegalArgumentException("Unknown state type");
      }
    }

    order.setStatus(status);

    // TODO: send message order cancel message to slack?


    OrderInquiry orderInquiry = null;
    if (purchase != null) {
      order.setPurchaseStatus(purchase.getId(), status);
      orderInquiry = orderInquiryRepository.save(new OrderInquiry(order, state, reason, purchase));
    } else {
      orderInquiry = orderInquiryRepository.save(new OrderInquiry(order, state, reason));
    }

    orderRepository.save(order);
    return orderInquiry;
  }

  @Transactional
  public void cancelPayment(Order order) {
    if (Order.ORDER_CANCELLING != order.getStatus()) {
      throw new BadRequestException("invalid_order_status", "invalid order status - " + order.getStatus());
    }

    Payment payment = paymentRepository.findById(order.getId()).orElseThrow(() -> new NotFoundException("payment_not_found", "invalid payment id"));

    String token = iamportService.getToken();
    iamportService.cancelPayment(token, payment.getPaymentId());

    log.debug("cancel order: {}", order);
    saveOrderAndPurchasesStatus(order, Order.ORDER_CANCELLED);

    payment.setState(Payment.STATE_CANCELLED);
    paymentRepository.save(payment);

    OrderInquiry orderInquiry = orderInquiryRepository.findByOrderAndCreatedBy(order, order.getCreatedBy()).orElseThrow(() -> new NotFoundException("inquire_not_found", "invalid inquire id"));
    orderInquiry.setCompleted(true);
    orderInquiryRepository.save(orderInquiry);
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

  @Transactional
  private void completeOrder(Order order) {
    saveOrderAndPurchasesStatus(order, Order.PAID);

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
