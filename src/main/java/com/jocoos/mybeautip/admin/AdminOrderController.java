package com.jocoos.mybeautip.admin;


import com.jocoos.mybeautip.exception.NotFoundException;
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
@RequestMapping("/api/admin/manual/orders")
public class AdminOrderController {

    private final static String CANCEL_REASON = "관리자에의한취소";

    private final OrderService orderService;
    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    public AdminOrderController(OrderService orderService,
                                OrderRepository orderRepository,
                                PaymentRepository paymentRepository) {
        this.orderService = orderService;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    /**
     * Order cancel by Admin
     * This API is used to cancel myBeautip Order and payment state without Iamport payment
     * It is used when inconsistency occurred between myBeautip and Iamport payment state
     */
    @PostMapping("/{id}/order_cancel")
    public ResponseEntity<OrderController.OrderInquiryInfo> orderCancelByAdmin(@PathVariable("id") Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("order_not_found", "Order not found: " + id));
        Payment payment = paymentRepository.findById(order.getId())
                .orElseThrow(() -> new NotFoundException("payment_not_found", "invalid payment id"));

        OrderInquiry inquiry = orderService.cancelOrderInquireByAdmin(order, payment, Byte.parseByte("0"), CANCEL_REASON);
        return new ResponseEntity<>(new OrderController.OrderInquiryInfo(inquiry), HttpStatus.OK);
    }

    @PostMapping("/{id}/payment_cancel")
    public ResponseEntity<OrderController.OrderInquiryInfo> cancelPaymentByAdmin(@PathVariable("id") Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("order_not_found", "Order not found: " + id));
        Payment payment = paymentRepository.findById(order.getId())
                .orElseThrow(() -> new NotFoundException("payment_not_found", "invalid payment id"));

        OrderInquiry inquiry = orderService.cancelPaymentByAdmin(order, payment, Byte.parseByte("0"), CANCEL_REASON);
        return new ResponseEntity<>(new OrderController.OrderInquiryInfo(inquiry), HttpStatus.OK);
    }

}
