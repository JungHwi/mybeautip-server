package com.jocoos.mybeautip.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.order.OrderService;
import com.jocoos.mybeautip.member.order.Purchase;
import com.jocoos.mybeautip.member.order.PurchaseRepository;
import com.jocoos.mybeautip.restapi.OrderController;

@Slf4j
@RestController
@RequestMapping("/api/admin/batch")
public class AdminBatchController {
  
  private final OrderService orderService;
  private final PurchaseRepository purchaseRepository;
  
  public AdminBatchController(OrderService orderService,
                              PurchaseRepository purchaseRepository) {
    this.orderService = orderService;
    this.purchaseRepository = purchaseRepository;
  }
  
  @PostMapping("/purchases/{id}/confirm")
  public ResponseEntity<OrderController.PurchaseInfo> confirmPurchase(@PathVariable("id") Long id) {
    Purchase purchase = purchaseRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("purchase_not_found", "Purchase not found"));
    
    if (purchase.getConfirmed()) {
      throw new BadRequestException("already_confirmed", "Already confirmed");
    }
    
    purchase = orderService.confirm(purchase);
    OrderController.PurchaseInfo info = new OrderController.PurchaseInfo(purchase);
    return new ResponseEntity<>(info, HttpStatus.OK);
  }
}
