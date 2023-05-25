package com.jocoos.mybeautip.domain.delivery.dto;

import java.util.Set;

public record DeleteDeliveryFeePolicyRequest(Set<Long> deliveryFeeIds) {
}
