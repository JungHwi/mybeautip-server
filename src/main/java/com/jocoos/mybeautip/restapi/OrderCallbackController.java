package com.jocoos.mybeautip.restapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.member.order.OrderService;

@Slf4j
@RestController
@RequestMapping("/api/1/orders")
public class OrderCallbackController {

  private final OrderService orderService;

  public OrderCallbackController(OrderService orderService) {
    this.orderService = orderService;
  }

  @GetMapping("/complete")
  public ResponseEntity<String> orderComplete(@RequestParam String impUid,
                                              @RequestParam Long merchantUid,
                                              @RequestParam Boolean impSuccess,
                                              @RequestParam(required = false) String errorMsg) {

    String html = orderService.complete(impUid, merchantUid, impSuccess, errorMsg);
    return new ResponseEntity<>(html, HttpStatus.OK);
  }
}
