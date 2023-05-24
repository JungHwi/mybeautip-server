package com.jocoos.mybeautip.domain.delivery.converter;

import com.jocoos.mybeautip.domain.delivery.dto.DeliveryFeePolicyListResponse;
import com.jocoos.mybeautip.domain.delivery.dto.DeliveryFeePolicyResponse;
import com.jocoos.mybeautip.domain.delivery.persistence.domain.DeliveryFeePolicy;
import com.jocoos.mybeautip.domain.delivery.vo.DeliveryFeePolicySearchResult;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DeliveryFeePolicyConverter {

    @Mappings({
            @Mapping(target = "companyId", source = "company.id")
    })
    DeliveryFeePolicyResponse converts(DeliveryFeePolicy entity);
    List<DeliveryFeePolicyListResponse> converts(List<DeliveryFeePolicySearchResult> results);
}
