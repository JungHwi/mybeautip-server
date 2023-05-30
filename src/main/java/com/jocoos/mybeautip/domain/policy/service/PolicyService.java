package com.jocoos.mybeautip.domain.policy.service;

import com.jocoos.mybeautip.domain.policy.converter.PolicyConverter;
import com.jocoos.mybeautip.domain.policy.dto.EditPolicyRequest;
import com.jocoos.mybeautip.domain.policy.dto.PolicyResponse;
import com.jocoos.mybeautip.domain.policy.persistence.domain.Policy;
import com.jocoos.mybeautip.domain.policy.service.dao.PolicyDao;
import com.jocoos.mybeautip.global.code.CountryCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PolicyService {

    private final PolicyDao dao;
    private final PolicyConverter converter;

    @Transactional(readOnly = true)
    public PolicyResponse get(CountryCode countryCode) {
        Policy policy = dao.get(countryCode);
        return converter.converts(policy);
    }

    @Transactional
    public PolicyResponse edit(CountryCode countryCode, EditPolicyRequest request) {
        Policy policy = dao.edit(countryCode, request);
        return converter.converts(policy);
    }
}
