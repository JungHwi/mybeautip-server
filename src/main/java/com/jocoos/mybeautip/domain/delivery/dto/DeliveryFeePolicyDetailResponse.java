package com.jocoos.mybeautip.domain.delivery.dto;

import com.jocoos.mybeautip.global.code.CountryCode;
import lombok.Builder;

@Builder
public record DeliveryFeePolicyDetailResponse(long id,
                                              CountryCode countryCode,
                                              String name,
                                              Integer threshold,
                                              int feeBelowThreshold,
                                              int feeAboveThreshold) {
}