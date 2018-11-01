package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.order.OrderRepository;
import com.jocoos.mybeautip.member.order.OrderService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
  private final OrderRepository orderRepository;

  public PublicPaymentController(OrderService orderService,
                                 OrderRepository orderRepository) {
    this.orderService = orderService;
    this.orderRepository = orderRepository;
  }

  @PostMapping(value = "/notification", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity notification(@RequestBody CreateNotificationReqeust request) {
    log.debug("request: {}", request);
    
    if (request.getImpUid() == null || !StringUtils.isNumeric(request.getMerchantUid())) {
      log.warn("Invalid import notification request: ", request.toString());
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
    
    return orderRepository.findById(Long.valueOf(request.getMerchantUid()))
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
