package com.jocoos.mybeautip.domain.policy.converter;

import com.jocoos.mybeautip.domain.policy.dto.PolicyResponse;
import com.jocoos.mybeautip.domain.policy.persistence.domain.Policy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PolicyConverter {

    PolicyResponse converts(Policy entity);
}
