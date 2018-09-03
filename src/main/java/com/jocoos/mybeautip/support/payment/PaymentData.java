package com.jocoos.mybeautip.support.payment;

import lombok.Data;

@Data
public class PaymentData {

  private String impUid;
  private String merchantUid;
  private String payMethod;
  private String pgProvider;
  private String pgTid;
  private Long amount;
  private Long cancelAmount;
  private String status;
  private Long paidAt;
  private Long failedAt;
  private String failReason;
  private String cancelReason;
  private String receiptUrl;
}
