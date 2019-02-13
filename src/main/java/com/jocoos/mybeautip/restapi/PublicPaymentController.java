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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

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
    HttpServletRequest httpServletRequest = ((ServletRequestAttributes)RequestContextHolder
        .currentRequestAttributes()).getRequest();
    String ip = httpServletRequest.getHeader("X-FORWARDED-FOR");
    log.debug("payments/notification request ip is " + ip);
    if (ip == null) {
      ip = httpServletRequest.getRemoteAddr();
      log.debug("payments/notification request remoteAddress is " + ip);
    }
    
    // Ref: https://docs.iamport.kr/tech/webhook
    String[] iamportWebHookClients = {"52.78.100.19", "52.78.48.223"};
    if (!StringUtils.containsAny(ip, iamportWebHookClients)) {
      log.warn("Invalid iamport notification request client ip: ", request.toString());
      return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
    
    
    log.info("payments/notification called: {}", request);
    
    if (request.getImpUid() == null || !StringUtils.isNumeric(request.getMerchantUid())) {
      log.warn("Invalid iamport notification request: ", request.toString());
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
