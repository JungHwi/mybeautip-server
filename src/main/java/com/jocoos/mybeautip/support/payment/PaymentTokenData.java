package com.jocoos.mybeautip.support.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentTokenData {

  @JsonProperty("access_token")
  private String accessToken;

  @JsonProperty("expired_at")
  private Long expiredAt;

  private Long now;
}
