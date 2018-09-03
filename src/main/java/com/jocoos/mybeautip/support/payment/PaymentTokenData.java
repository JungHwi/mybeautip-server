package com.jocoos.mybeautip.support.payment;

import lombok.Data;

@Data
public class PaymentTokenData {
  private String accessToken;
  private Long expiredAt;
  private Long now;
}
