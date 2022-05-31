package com.jocoos.mybeautip.support.payment;

import lombok.Data;

@Data
public class PaymentTokenResponse {
    private Integer code;
    private String message;
    private PaymentTokenData response;
}
