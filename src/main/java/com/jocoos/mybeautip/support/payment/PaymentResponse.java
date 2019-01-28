package com.jocoos.mybeautip.support.payment;

import lombok.Data;

@Data
public class PaymentResponse {
  private Integer code;
  private String message;
  private PaymentData response;
}
