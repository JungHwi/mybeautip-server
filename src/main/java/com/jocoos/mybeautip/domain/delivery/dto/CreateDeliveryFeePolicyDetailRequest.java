package com.jocoos.mybeautip.domain.delivery.dto;

import com.jocoos.mybeautip.global.code.CountryCode;
import lombok.Builder;

@Builder
public record CreateDeliveryFeePolicyDetailRequest(CountryCode countryCode,
                                                   String name,
                                                   Integer threshold,
                                                   int feeBelowThreshold,
                                                   int feeAboveThreshold) {
}