package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.order.OrderRepository;
import com.jocoos.mybeautip.member.order.OrderService;
import com.jocoos.mybeautip.support.slack.SlackService;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(path = "/api/1/payments", produces = MediaType.APPLICATION_JSON_VALUE)
public class PublicPaymentController {

  private final OrderService orderService;
  private final SlackService slackService;
  private final OrderRepository orderRepository;

  public PublicPaymentController(OrderService orderService,
                                 SlackService slackService,
                                 OrderRepository orderRepository) {
    this.orderService = orderService;
    this.slackService = slackService;
    this.orderRepository = orderRepository;
  }

  @PostMapping(value = "/notification", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity notification(@RequestBody CreateNotificationReqeust request) {
    log.info("payments/notification called: {}", request);
    
    if (request.getImpUid() == null || request.getMerchantUid() == null) {
      log.warn("Invalid iamport notification request: ", request.toString());
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
  
    long orderId;
    try {
      orderId = orderService.parseOrderId(request.getMerchantUid());
    } catch (NumberFormatException e) {
      log.warn("Invalid merchant_uid: %s, imp_uid: %s", request.getMerchantUid(), request.getImpUid());
      slackService.sendForImportMerchantIdFormatException(request.getMerchantUid(), request.getImpUid());
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
    
    return orderRepository.findById(orderId)
       .map(order -> {
         orderService.notifyPayment(order, request.getStatus(), request.getImpUid());
         return new ResponseEntity(HttpStatus.NO_CONTENT);
       })
       .orElseThrow(() -> new NotFoundException("order_not_found", "invalid order id"));
  }


  @NoArgsConstructor
  @Data
  static class CreateNotificationReqeust {
    private String impUid;
    private String merchantUid;
    private String status;
  }
}
