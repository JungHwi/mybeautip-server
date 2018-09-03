package com.jocoos.mybeautip.member.order;

import javax.transaction.Transactional;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.restapi.OrderController;
import com.jocoos.mybeautip.support.payment.IamportService;
import com.jocoos.mybeautip.support.payment.PaymentResponse;

@Slf4j
@Service
public class OrderService {

  private final SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmssSSS");
  private final OrderRepository orderRepository;
  private final DeliveryRepository deliveryRepository;
  private final PaymentRepository paymentRepository;
  private final IamportService iamportService;

  public OrderService(OrderRepository orderRepository,
                      DeliveryRepository deliveryRepository,
                      PaymentRepository paymentRepository,
                      IamportService iamportService) {
    this.orderRepository = orderRepository;
    this.deliveryRepository = deliveryRepository;
    this.paymentRepository = paymentRepository;
    this.iamportService = iamportService;
  }

  @Transactional
  public Order create(OrderController.CreateOrderRequest request) {
    Order order = new Order();
    BeanUtils.copyProperties(request, order);
    order.setGoodsCount(Optional.of(request.getPurchases()).map(List::size).orElse(0));
    order.setNumber(orderNumber());

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
      Purchase purchase = new Purchase(order.getId());
      BeanUtils.copyProperties(p, purchase);
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
            return errorHtml(id, message);
          }

          checkPayment(order.getId(), uid);

          Payment payment = updatePaymentState(order.getId(), uid, Payment.STATE_PURCHASED, message);
          if ((payment.getState() & Payment.STATE_PAID) != 0) {
            if (!Order.PAID.equals(order.getStatus())) {
              completeOrder(order);
            }
          } else {
            return errorHtml(id, message);
          }
          return successHtml(id);
       }).orElseThrow(() -> new NotFoundException("order_not_found", "invalid order id"));
  }

  private Payment checkPayment(Long orderId, String paymentId) {
    return paymentRepository.findById(orderId)
       .map(payment -> {
         int state = 0;
         String token = iamportService.getToken();
         log.debug("access token:{}", token);
         PaymentResponse response = iamportService.getPayment(token, paymentId);

         if (response.getCode() == 0) {
           state = stateValue(response.getResponse().getStatus());
           if (state == Payment.STATE_PAID && response.getResponse().getAmount() == payment.getPrice()) {
             payment.setMessage(response.getResponse().getStatus());
             payment.setReceipt(response.getResponse().getReceiptUrl());
           } else {
             payment.setMessage(response.getResponse().getFailReason());
           }
         } else {
           payment.setMessage(response.getMessage());
         }

         payment.setState(state | Payment.STATE_PURCHASED);
         payment.setPaymentId(paymentId);
         return paymentRepository.save(payment);
       })
    .orElseThrow(() -> new NotFoundException("payment_not_found", "invalid order id or payment id"));
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
    order.setStatus(Order.PAID);
    orderRepository.save(order);

    // TODO: Update Coin use ?
    // TODO: Notify ?
    // TODO: Send email ?
    // TODO: Send To Slack Message?
  }

  private String successHtml(Long id) {
    return new StringBuilder("<script language=\"javascript\">")
       .append("mybeautip.success(" + id + ");")
       .append("</script>").toString();
  }

  private String errorHtml(Long id, String message) {
    return new StringBuilder("<script language=\"javascript\">")
       .append("mybeautip.fail(" + id + ", \"" + message + "\");")
       .append("</script>").toString();
  }

  private String orderNumber() {
    return df.format(new Date()) + new Random().nextInt(10);
  }
}
