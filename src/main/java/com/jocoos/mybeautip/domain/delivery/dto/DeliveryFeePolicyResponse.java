package com.jocoos.mybeautip.domain.delivery.dto;

import com.jocoos.mybeautip.domain.delivery.code.DeliveryFeeStatus;
import com.jocoos.mybeautip.domain.delivery.code.DeliveryFeeType;
import com.jocoos.mybeautip.domain.delivery.code.DeliveryMethod;
import com.jocoos.mybeautip.domain.delivery.code.PaymentOption;
import lombok.Builder;

import java.util.List;

@Builder
public record DeliveryFeePolicyResponse(long id,
                                        String code,
                                        long companyId,
                                        String name,
                                        DeliveryFeeStatus status,
                                        DeliveryFeeType type,
                                        Boolean isDefault,
                                        DeliveryMethod deliveryMethod,
                                        PaymentOption paymentOption,
                                        List<DeliveryFeePolicyDetailResponse> details) {
}
