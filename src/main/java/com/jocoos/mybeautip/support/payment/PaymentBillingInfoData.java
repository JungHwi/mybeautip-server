package com.jocoos.mybeautip.support.payment;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaymentBillingInfoData {
    @JsonProperty("customer_uid")
    private String customerId;
    @JsonProperty("card_name")
    private String cardName;
    @JsonProperty("card_number")
    private String cardNumber;
}
