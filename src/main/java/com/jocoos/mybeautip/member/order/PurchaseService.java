package com.jocoos.mybeautip.member.order;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PurchaseService {
  
  private final PurchaseRepository purchaseRepository;
  private final OrderRepository orderRepository;
  
  public PurchaseService(PurchaseRepository purchaseRepository,
                         OrderRepository orderRepository) {
    this.purchaseRepository = purchaseRepository;
    this.orderRepository = orderRepository;
  }
  
  @Transactional
  public Purchase completeDelivery(Purchase purchase, Order order, Date deliveredAt) {
    purchase.setDeliveredAt(deliveredAt);
    purchase = purchaseRepository.save(purchase);
    
    // Complete delivery of Order after when all purchases related order are already had delivered
    List<Purchase> purchases = order.getPurchases();
    for (Purchase p : purchases) {
      if (p.getDeliveredAt() == null && !p.equals(purchase)) {
        return purchase;
      }
    }
    
    order.setDeliveredAt(deliveredAt);
    orderRepository.save(order);
    return purchase;
  }
}
