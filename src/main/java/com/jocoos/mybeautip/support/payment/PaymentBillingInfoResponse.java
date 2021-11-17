package com.jocoos.mybeautip.support.payment;

import lombok.Data;

@Data
public class PaymentBillingInfoResponse {
  private Integer code;
  private String message;
  private PaymentBillingInfoData response;
}