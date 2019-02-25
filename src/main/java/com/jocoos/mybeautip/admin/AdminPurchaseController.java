package com.jocoos.mybeautip.admin;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.order.OrderService;
import com.jocoos.mybeautip.member.order.Purchase;
import com.jocoos.mybeautip.member.order.PurchaseRepository;
import com.jocoos.mybeautip.restapi.OrderController;

@Slf4j
@RestController
@RequestMapping("/api/admin/manual/purchases")
public class AdminPurchaseController {
  
  private final OrderService orderService;
  private final PurchaseRepository purchaseRepository;
  
  public AdminPurchaseController(OrderService orderService,
                                 PurchaseRepository purchaseRepository) {
    this.orderService = orderService;
    this.purchaseRepository = purchaseRepository;
  }
  
  @PatchMapping("/{id}")
  public ResponseEntity<OrderController.PurchaseInfo> update(@PathVariable("id") Long id,
                                                             @RequestBody UpdatePurchaseRequest request) {
    if (request.getConfirmed() == null || !request.getConfirmed()) {
      throw new BadRequestException("invalid_request", "Valid 'confiremd' value is true");
    } else {
      Purchase purchase = purchaseRepository.findById(id)
          .orElseThrow(() -> new NotFoundException("purchase_not_found", "Purchase not found: " + id));
      
      if (purchase.getConfirmed()) {
        throw new BadRequestException("already_confirmed", "Already confirmed, id: " + id);
      }
      Purchase result = orderService.confirm(purchase);
      OrderController.PurchaseInfo info = new OrderController.PurchaseInfo(result);
      return new ResponseEntity<>(info, HttpStatus.OK);
    }
  }
  
  @Data
  private static class UpdatePurchaseRequest {
    private Boolean confirmed;
  }
}
