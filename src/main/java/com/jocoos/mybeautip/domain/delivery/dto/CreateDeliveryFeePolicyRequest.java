package com.jocoos.mybeautip.domain.delivery.dto;

import com.jocoos.mybeautip.domain.delivery.code.DeliveryFeeStatus;
import com.jocoos.mybeautip.domain.delivery.code.DeliveryFeeType;
import com.jocoos.mybeautip.domain.delivery.code.DeliveryMethod;
import com.jocoos.mybeautip.domain.delivery.code.PaymentOption;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
public class CreateDeliveryFeePolicyRequest {

    private long companyId;

    @Setter
    private String code;

    private String name;

    private DeliveryFeeStatus status;

    private DeliveryFeeType type;

    private DeliveryMethod deliveryMethod;

    private PaymentOption paymentOption;

    private List<CreateDeliveryFeePolicyDetailRequest> details;

    @Builder
    public CreateDeliveryFeePolicyRequest(long companyId, String name, DeliveryFeeStatus status, DeliveryFeeType type, DeliveryMethod deliveryMethod, PaymentOption paymentOption, List<CreateDeliveryFeePolicyDetailRequest> details) {
        this.companyId = companyId;
        this.name = name;
        this.status = status == null ? DeliveryFeeStatus.ACTIVE : status;
        this.type = type;
        this.deliveryMethod = deliveryMethod == null ? DeliveryMethod.COURIER : deliveryMethod;
        this.paymentOption = paymentOption;
        this.details = details;
    }
}