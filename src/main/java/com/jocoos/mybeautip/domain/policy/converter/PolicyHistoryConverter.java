package com.jocoos.mybeautip.domain.policy.converter;

import com.jocoos.mybeautip.domain.policy.dto.PolicyHistoryListResponse;
import com.jocoos.mybeautip.domain.policy.persistence.domain.PolicyHistory;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PolicyHistoryConverter {

    List<PolicyHistoryListResponse> converts(List<PolicyHistory> policyHistoryList);
}
