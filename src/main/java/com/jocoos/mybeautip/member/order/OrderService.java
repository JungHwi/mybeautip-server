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

import com.jocoos.mybeautip.restapi.OrderController;

@Slf4j
@Service
public class OrderService {

  private final SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmssSSS");
  private final OrderRepository orderRepository;
  private final DeliveryRepository deliveryRepository;
  private final PaymentRepository paymentRepository;

  public OrderService(OrderRepository orderRepository,
                      DeliveryRepository deliveryRepository,
                      PaymentRepository paymentRepository) {
    this.orderRepository = orderRepository;
    this.deliveryRepository = deliveryRepository;
    this.paymentRepository = paymentRepository;
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

  private String orderNumber() {
    return df.format(new Date()) + new Random().nextInt(10);
  }
}
