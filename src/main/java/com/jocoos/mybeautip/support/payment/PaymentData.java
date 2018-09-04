package com.jocoos.mybeautip.support.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentData {

  @JsonProperty("imp_uid")
  private String impUid;
  @JsonProperty("merchant_uid")
  private String merchantUid;
  @JsonProperty("pay_method")
  private String payMethod;
  @JsonProperty("pg_provider")
  private String pgProvider;
  @JsonProperty("pg_tid")
  private String pgTid;
  private Long amount;
  @JsonProperty("cancel_amount")
  private Long cancelAmount;
  private String status;
  @JsonProperty("paid_at")
  private Long paidAt;
  @JsonProperty("failed_at")
  private Long failedAt;
  @JsonProperty("fail_reason")
  private String failReason;
  @JsonProperty("cancel_reason")
  private String cancelReason;
  @JsonProperty("receipt_url")
  private String receiptUrl;
}
