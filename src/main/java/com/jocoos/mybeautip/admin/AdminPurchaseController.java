package com.jocoos.mybeautip.admin;


import java.util.Date;

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
import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.member.order.OrderRepository;
import com.jocoos.mybeautip.member.order.OrderService;
import com.jocoos.mybeautip.member.order.Purchase;
import com.jocoos.mybeautip.member.order.PurchaseRepository;
import com.jocoos.mybeautip.member.order.PurchaseService;
import com.jocoos.mybeautip.restapi.OrderController;

@Slf4j
@RestController
@RequestMapping("/api/admin/manual/purchases")
public class AdminPurchaseController {
  
  private final OrderService orderService;
  private final PurchaseService purchaseService;
  private final OrderRepository orderRepository;
  private final PurchaseRepository purchaseRepository;
  
  public AdminPurchaseController(OrderService orderService,
                                 PurchaseService purchaseService,
                                 OrderRepository orderRepository,
                                 PurchaseRepository purchaseRepository) {
    this.orderService = orderService;
    this.purchaseService = purchaseService;
    this.orderRepository = orderRepository;
    this.purchaseRepository = purchaseRepository;
  }
  
  @PatchMapping("/{id}/delivered")
  public ResponseEntity<OrderController.PurchaseInfo> completeDelivered(@PathVariable("id") Long id,
                                                                        @RequestBody UpdatePurchaseRequest request) {
    Purchase purchase = purchaseRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("purchase_not_found", "Purchase not found"));
    Order order = orderRepository.findById(purchase.getOrderId())
        .orElseThrow(() -> new NotFoundException("order_not_found", "Order not found"));
    
    if (purchase.isDelivered()) {
      throw new BadRequestException("already_delivered", "Already delivered");
    }
    if (!purchase.isDelivering()) {
      throw new BadRequestException("required purchase status - delivering");
    }
    if (order.isDelivering()) {
      log.warn("something wrong", "Order state cannot be 'delivered' until all state of purchases has been 'delivered'.");
      throw new BadRequestException("invalid_order_state", "required order status - delivering");
    }
    
    // Complete purchase & order delivery
    Date deliveredAt = (request.getDeliveredAt()) == null ? new Date() : request.getDeliveredAt();
    purchase = purchaseService.completeDelivery(purchase, order, deliveredAt);
    OrderController.PurchaseInfo info = new OrderController.PurchaseInfo(purchase);
    return new ResponseEntity<>(info, HttpStatus.OK);
  }
  
  @Data
  private static class UpdatePurchaseRequest {
    private Date deliveredAt;
  }
}
