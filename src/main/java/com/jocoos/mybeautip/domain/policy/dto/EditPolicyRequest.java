package com.jocoos.mybeautip.domain.policy.dto;

import lombok.Builder;

@Builder
public record EditPolicyRequest(String deliveryPolicy,
                                String claimPolicy) {
}
