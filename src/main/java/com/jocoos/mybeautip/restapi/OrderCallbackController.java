package com.jocoos.mybeautip.restapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.order.OrderRepository;
import com.jocoos.mybeautip.member.order.OrderService;
import com.jocoos.mybeautip.support.payment.IamportService;
import com.jocoos.mybeautip.support.payment.PaymentResponse;
import com.jocoos.mybeautip.support.slack.SlackService;

@Slf4j
@RestController
@RequestMapping("/api/1/orders")
public class OrderCallbackController {
  
  private final IamportService iamportService;
  private final OrderService orderService;
  private final SlackService slackService;
  private final OrderRepository orderRepository;

  public OrderCallbackController(IamportService iamportService,
                                 OrderService orderService,
                                 SlackService slackService,
                                 OrderRepository orderRepository) {
    this.iamportService = iamportService;
    this.orderService = orderService;
    this.slackService = slackService;
    this.orderRepository = orderRepository;
  }

  @GetMapping("/complete")
  public ResponseEntity<String> orderComplete(@RequestParam(name = "imp_uid") String impUid,
                                              @RequestParam(name = "merchant_uid") String merchantUid,
                                              @RequestParam(name = "imp_success") Boolean impSuccess, // Deprecated
                                              @RequestParam(name = "error_msg", required = false) String errorMsg) {

    log.info(String.format("OrderCallbackComplete called: impUid=%s, merchantUid=%s", impUid, merchantUid));
    String html;
    if (impUid == null) { // Import payment Id
      html = getErrorHtml(merchantUid, "impUid is null");
      log.warn("OrderCallbackComplete response: " + html);
      return new ResponseEntity<>(html, HttpStatus.OK);
    }
  
    // Get payment info from Import Service using REST API
    // Refer: https://api.iamport.kr/
    String token = iamportService.getToken();
    PaymentResponse response = iamportService.getPayment(token, impUid);
    if (response.getCode() != 0 || response.getResponse() == null) {
      html = getErrorHtml(merchantUid, response.getMessage());
      log.warn("invalid_iamport_response, OrderCallbackComplete response: " + html);
      slackService.sendForImportGetPaymentException(impUid, response.toString());
      return new ResponseEntity<>(html, HttpStatus.OK);
    }
    
    if (response.getCode() == 0 && !response.getResponse().getStatus().equals("paid")) {
      String message = response.getMessage();
      switch (response.getResponse().getStatus()) {
        case "cancelled":
          message = response.getResponse().getCancelReason();
          break;
        case "failed":
          message = response.getResponse().getFailReason();
          break;
        default:
          break;
      }
      html = getErrorHtml(merchantUid, message);
      log.warn("invalid_iamport_response, OrderCallbackComplete response: " + html);
      return new ResponseEntity<>(html, HttpStatus.OK);
    }
    
    long orderId;
    try {
      orderId = orderService.parseOrderId(merchantUid);
    } catch (NumberFormatException e) {
      html = getErrorHtml(merchantUid, String.format("Invalid merchant_uid: %s", merchantUid));
      log.warn("OrderCallbackComplete response, invalid merchant_uid: " + html);
      return new ResponseEntity<>(html, HttpStatus.OK);
    }
  
    html = orderRepository.findByIdAndDeletedAtIsNull(orderId)
        .map(order -> {
          if (order.getPayment().getPrice() == response.getResponse().getAmount().longValue()) {
            return getSuccessHtml(merchantUid);
          } else {
            return getErrorHtml(merchantUid, String.format("Unexpected amount - expected:%d, actual:%d",
                order.getPayment().getPrice(), response.getResponse().getAmount()));
          }
        })
        .orElseThrow(() -> new NotFoundException("order_not_found", "invalid order id"));
  
    log.info("OrderCallbackComplete response: " + html);
    return new ResponseEntity<>(html, HttpStatus.OK);
  }
  
  private String getSuccessHtml(String id) {
    return "<script language=\"javascript\">" +
        "mybeautip.success(\"" + id + "\");" +
        "</script>";
  }
  
  private String getErrorHtml(String id, String message) {
    return "<script language=\"javascript\">" +
        "mybeautip.fail(\"" + id + "\", \"" + message + "\");" +
        "</script>";
  }
}