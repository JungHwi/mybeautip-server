package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import com.jocoos.mybeautip.member.order.*;
import com.jocoos.mybeautip.restapi.OrderController;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
                .orElseThrow(() -> new NotFoundException(ErrorCode.PURCHASE_NOT_FOUND, "Purchase not found"));

        if (purchase.isConfirmed()) {
            throw new BadRequestException(ErrorCode.ALREADY_CONFIRMED, "Already confirmed");
        }

        purchase = orderService.confirmPurchase(purchase);
        OrderController.PurchaseInfo info = new OrderController.PurchaseInfo(purchase);
        return new ResponseEntity<>(info, HttpStatus.OK);
    }

    @PostMapping("/orders/{id}/confirm")
    public ResponseEntity<OrderController.OrderInfo> confirmOrder(@PathVariable("id") Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.ORDER_NOT_FOUND, "Order not found"));

        if (order.isConfirmed()) {
            throw new BadRequestException(ErrorCode.ALREADY_CONFIRMED, "Already confirmed");
        }

        order = orderService.confirmOrder(order);
        OrderController.OrderInfo info = new OrderController.OrderInfo(order);
        return new ResponseEntity<>(info, HttpStatus.OK);
    }
}
