package com.jocoos.mybeautip.domain.delivery.dto;

import com.jocoos.mybeautip.domain.delivery.code.DeliveryFeeSearchField;
import com.jocoos.mybeautip.domain.delivery.code.DeliveryFeeType;
import org.springframework.data.domain.Pageable;

public record DeliveryFeePolicySearchRequest(DeliveryFeeSearchField searchField,
                                             String searchText,
                                             DeliveryFeeType type,
                                             Pageable pageable) {
}
