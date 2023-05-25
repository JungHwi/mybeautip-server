package com.jocoos.mybeautip.domain.delivery.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.delivery.code.DeliveryFeeType;
import com.jocoos.mybeautip.domain.delivery.code.DeliveryMethod;
import com.jocoos.mybeautip.domain.delivery.code.PaymentOption;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Builder
public record DeliveryFeePolicyListResponse(long id,
                                            String name,
                                            String companyCode,
                                            String companyName,
                                            DeliveryFeeType type,
                                            Boolean isDefault,
                                            DeliveryMethod deliveryMethod,
                                            PaymentOption paymentOption,
                                            List<DeliveryFeePolicyDetailResponse> details,
                                            @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT) ZonedDateTime createdAt) {
}
