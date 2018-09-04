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

import com.jocoos.mybeautip.exception.BadRequestException;
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
  private final OrderInquiryRepository orderInquiryRepository;
  private final IamportService iamportService;

  public OrderService(OrderRepository orderRepository,
                      DeliveryRepository deliveryRepository,
                      PaymentRepository paymentRepository,
                      OrderInquiryRepository orderInquiryRepository,
                      IamportService iamportService) {
    this.orderRepository = orderRepository;
    this.deliveryRepository = deliveryRepository;
    this.paymentRepository = paymentRepository;
    this.orderInquiryRepository = orderInquiryRepository;
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
            return getErrorHtml(id, message);
          }

          Payment payment = checkPaymentAndUpdate(order.getId(), uid);
          if ((payment.getState() & Payment.STATE_PAID) != 0) {
            if (!Order.PAID.equals(order.getStatus())) {
              completeOrder(order);
            }
          } else {
            return getErrorHtml(id, message);
          }
          return getSuccessHtml(id);
       }).orElseThrow(() -> new NotFoundException("order_not_found", "invalid order id"));
  }

  @Transactional
  public OrderInquiry inquireOrder(Order order, Byte state, String reason) {
    if (Order.ORDER_CANCELLED.equals(order.getStatus()) || Order.ORDER_CANCELLING.equals(order.getStatus())) {
      throw new BadRequestException("order_cancel_duplicated", "Already requested order canceling");
    }

    switch (state) {
      case 0: {
        if (Order.PAID.equals(order.getStatus()) || Order.PREPARING.equals(order.getStatus())) {
          order.setStatus(Order.ORDER_CANCELLING);
        } else {
          throw new BadRequestException("Can not cancel payment -" + order.getStatus());
        }
        break;
      }
      case 1: {
        if (Order.DELIVERED.equals(order.getStatus())) {
          order.setStatus(Order.ORDER_EXCHANGING);
        } else {
          throw new BadRequestException("invalid order exchange inquire -" + order.getStatus());
        }
        break;
      }
      case 2: {
        if (Order.DELIVERED.equals(order.getStatus())) {
          order.setStatus(Order.ORDER_RETURNING);
        } else {
          throw new BadRequestException("invalid order return inquire -" + order.getStatus());
        }
        break;
      }
      default: {
        throw new IllegalArgumentException("Unknown state type");
      }
    }

    // TODO: send message order cancel message to slack?

    orderRepository.save(order);
    return orderInquiryRepository.save(new OrderInquiry(order, state, reason));
  }

  @Transactional
  public void cancelPayment(Order order) {
    if (!Order.ORDER_CANCELLING.equals(order)) {
      throw new BadRequestException("invalid_order_status", "invalid order status - " + order.getStatus());
    }

    String token = iamportService.getToken();

    Payment payment = paymentRepository.findById(order.getId()).orElseThrow(() -> new NotFoundException("payment_not_found", "invalid payment id"));
    iamportService.cancelPayment(token, payment.getPaymentId());

    order.setStatus(Order.ORDER_CANCELLED);
    orderRepository.save(order);

    payment.setState(Payment.STATE_CANCELLED);
    paymentRepository.save(payment);

    OrderInquiry orderInquiry = orderInquiryRepository.findById(order.getId()).orElseThrow(() -> new NotFoundException("inquire_not_found", "invalid inquire id"));
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
           if (state == Payment.STATE_PAID && response.getResponse().getAmount() == payment.getPrice()) {
             payment.setMessage(response.getResponse().getStatus());
             payment.setReceipt(response.getResponse().getReceiptUrl());
             payment.setState(state | Payment.STATE_PURCHASED);
           } else {
             String failReason = response.getResponse().getFailReason() == null ? "invalid payment price or state" : response.getResponse().getFailReason();
             log.warn("fail reason: {}", failReason);
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
