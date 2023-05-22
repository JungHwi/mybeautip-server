package com.jocoos.mybeautip.domain.delivery.converter;

import com.jocoos.mybeautip.domain.delivery.dto.DeliveryFeePolicyResponse;
import com.jocoos.mybeautip.domain.delivery.persistence.domain.DeliveryFeePolicy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DeliveryFeePolicyConverter {

    DeliveryFeePolicyResponse converts(DeliveryFeePolicy entity);
}
