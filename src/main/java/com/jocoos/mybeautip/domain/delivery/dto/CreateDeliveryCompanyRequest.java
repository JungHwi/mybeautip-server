package com.jocoos.mybeautip.domain.delivery.dto;

import com.jocoos.mybeautip.domain.delivery.code.DeliveryCompanyStatus;
import lombok.Builder;

@Builder
public record CreateDeliveryCompanyRequest(String name,
                                           DeliveryCompanyStatus status,
                                           String url) {
}
