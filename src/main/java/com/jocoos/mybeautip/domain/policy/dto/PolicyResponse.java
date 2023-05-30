package com.jocoos.mybeautip.domain.policy.dto;

import com.jocoos.mybeautip.global.code.CountryCode;
import lombok.Builder;

@Builder
public record PolicyResponse(CountryCode countryCode,
                             String deliveryPolicy,
                             String claimPolicy) {
}
