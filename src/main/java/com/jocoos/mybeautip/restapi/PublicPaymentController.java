package com.jocoos.mybeautip.restapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.order.OrderRepository;
import com.jocoos.mybeautip.member.order.OrderService;

@Slf4j
@RestController
@RequestMapping("/api/1/payments")
public class PublicPaymentController {

  private final OrderService orderService;
  private final OrderRepository orderRepository;

  public PublicPaymentController(OrderService orderService,
                                 OrderRepository orderRepository) {
    this.orderService = orderService;
    this.orderRepository = orderRepository;
  }

  @PostMapping(name = "/notification")
  public ResponseEntity notification(@RequestBody CreateNotificationReqeust reqeust) {
    log.debug("request: {}", reqeust);

    if (StringUtils.isNumeric(reqeust.getImpUid())) {
      Long id = Long.parseLong(reqeust.getImpUid());
      return orderRepository.findById(id)
         .map(order -> {
           orderService.notifyPayment(order, reqeust.getStatus(), reqeust.getImpUid());
           return new ResponseEntity(HttpStatus.NO_CONTENT);
         })
         .orElseThrow(() -> new NotFoundException("order_not_found", "invalid order id"));
    }
    return new ResponseEntity(HttpStatus.BAD_REQUEST);
  }


  @NoArgsConstructor
  @Data
  static class CreateNotificationReqeust {
    private String impUid;
    private String merchantUid;
    private String status;
  }
}
