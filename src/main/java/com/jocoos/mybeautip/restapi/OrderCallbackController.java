package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.order.OrderRepository;
import com.jocoos.mybeautip.support.payment.IamportService;
import com.jocoos.mybeautip.support.payment.PaymentResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/1/orders")
public class OrderCallbackController {

  private final IamportService iamportService;
  private final OrderRepository orderRepository;

  public OrderCallbackController(IamportService iamportService,
                                 OrderRepository orderRepository) {
    this.iamportService = iamportService;
    this.orderRepository = orderRepository;
  }

  @GetMapping("/complete")
  public ResponseEntity<String> orderComplete(@RequestParam(name = "imp_uid") String impUid,
                                              @RequestParam(name = "merchant_uid") Long merchantUid,
                                              @RequestParam(name = "imp_success") Boolean impSuccess, // Deprecated
                                              @RequestParam(name = "error_msg", required = false) String errorMsg) {

    log.info(String.format("OrderCallbackComplete called: impUid=%s, merchantUid=%d", impUid, merchantUid));
    String html;
    if (impUid == null) { // Import payment Id
      html = getErrorHtml(merchantUid, "impUid is null");
      log.debug("OrderCallbackComplete response: " + html);
      return new ResponseEntity<>(html, HttpStatus.OK);
    }
  
    // Get payment info from Import Service using REST API
    String token = iamportService.getToken();
    PaymentResponse response = iamportService.getPayment(token, impUid);
    if (response.getCode() != 0 || response.getResponse() == null) {
      html = getErrorHtml(merchantUid, response.getMessage());
      log.debug("OrderCallbackComplete response: " + html);
      return new ResponseEntity<>(html, HttpStatus.OK);
    }
  
    html = orderRepository.findByIdAndDeletedAtIsNull(merchantUid)
        .map(order -> {
          if (order.getPayment().getPrice() == response.getResponse().getAmount().longValue()) {
            return getSuccessHtml(merchantUid);
          } else {
            return getErrorHtml(merchantUid, String.format("Unexpected amount - expected:%d, actual:%d",
                order.getPayment().getPrice(), response.getResponse().getAmount()));
          }
        })
        .orElseThrow(() -> new NotFoundException("order_not_found", "invalid order id"));
  
    log.debug("OrderCallbackComplete response: " + html);
    return new ResponseEntity<>(html, HttpStatus.OK);
  }
  
  private String getSuccessHtml(Long id) {
    return "<script language=\"javascript\">" +
        "mybeautip.success(" + id + ");" +
        "</script>";
  }
  
  private String getErrorHtml(Long id, String message) {
    return "<script language=\"javascript\">" +
        "mybeautip.fail(" + id + ", \"" + message + "\");" +
        "</script>";
  }
}