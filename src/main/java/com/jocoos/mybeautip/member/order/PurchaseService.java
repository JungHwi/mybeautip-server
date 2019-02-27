package com.jocoos.mybeautip.member.order;

import javax.transaction.Transactional;
import java.util.Date;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PurchaseService {
  
  private final PurchaseRepository purchaseRepository;
  
  public PurchaseService(PurchaseRepository purchaseRepository) {
    this.purchaseRepository = purchaseRepository;
  }
  
  @Transactional
  public Purchase completeDelivery(Purchase purchase, Date deliveredAt) {
    purchase.setDeliveredAt(deliveredAt);
    return purchaseRepository.save(purchase);
  }
}
