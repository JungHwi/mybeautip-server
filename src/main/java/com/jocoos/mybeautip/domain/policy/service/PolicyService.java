package com.jocoos.mybeautip.domain.policy.service;

import com.jocoos.mybeautip.domain.policy.converter.PolicyConverter;
import com.jocoos.mybeautip.domain.policy.dto.EditPolicyRequest;
import com.jocoos.mybeautip.domain.policy.dto.PolicyResponse;
import com.jocoos.mybeautip.domain.policy.persistence.domain.Policy;
import com.jocoos.mybeautip.domain.policy.service.dao.PolicyDao;
import com.jocoos.mybeautip.global.code.CountryCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PolicyService {

    private final PolicyDao dao;
    private final PolicyConverter converter;

    public PolicyResponse edit(CountryCode countryCode, EditPolicyRequest request) {
        Policy policy = dao.edit(countryCode, request);
        return converter.converts(policy);
    }
}
