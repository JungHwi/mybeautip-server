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
import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.member.order.OrderRepository;
import com.jocoos.mybeautip.member.order.OrderService;
import com.jocoos.mybeautip.member.order.Purchase;
import com.jocoos.mybeautip.member.order.PurchaseRepository;
import com.jocoos.mybeautip.restapi.OrderController;

@Slf4j
@RestController
@RequestMapping("/api/admin/batch")
public class AdminBatchController {
  
  private final OrderService orderService;
  private final OrderRepository orderRepository;
  private final PurchaseRepository purchaseRepository;
  
  public AdminBatchController(OrderService orderService,
                              OrderRepository orderRepository,
                              PurchaseRepository purchaseRepository) {
    this.orderService = orderService;
    this.orderRepository = orderRepository;
    this.purchaseRepository = purchaseRepository;
  }
  
  @PostMapping("/purchases/{id}/confirm")
  public ResponseEntity<OrderController.PurchaseInfo> confirmPurchase(@PathVariable("id") Long id) {
    Purchase purchase = purchaseRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("purchase_not_found", "Purchase not found"));
    
    if (purchase.isConfirmed()) {
      throw new BadRequestException("already_confirmed", "Already confirmed");
    }
  
    if (!purchase.isDelivered()) {
      throw new BadRequestException("invalid_state", "Current order state is not 'delivered'" + purchase.getState());
    }
    
    purchase = orderService.confirmPurchase(purchase);
    OrderController.PurchaseInfo info = new OrderController.PurchaseInfo(purchase);
    return new ResponseEntity<>(info, HttpStatus.OK);
  }
  
  @PostMapping("/orders/{id}/confirm")
  public ResponseEntity<OrderController.OrderInfo> confirmOrder(@PathVariable("id") Long id) {
    Order order = orderRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("order_not_found", "Order not found"));
    
    if (order.isConfirmed()) {
      throw new BadRequestException("already_confirmed", "Already confirmed");
    }
    
    if (!order.isDelivered()) {
      throw new BadRequestException("invalid_state", "Current order state is not 'delivered', current state: " + order.getState());
    }
    
    order = orderService.confirmOrder(order);
    OrderController.OrderInfo info = new OrderController.OrderInfo(order);
    return new ResponseEntity<>(info, HttpStatus.OK);
  }
}
