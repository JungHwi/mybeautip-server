package com.jocoos.mybeautip.support.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VbankResponse {
  private int code;
  private String message;
  private VbankData response;
  
  @Data
  public class VbankData {
    @JsonProperty("bank_holder")
    private String bankHolder;
  }
}
