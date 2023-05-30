package com.jocoos.mybeautip.domain.policy.service.dao;

import com.jocoos.mybeautip.domain.policy.dto.EditPolicyRequest;
import com.jocoos.mybeautip.domain.policy.persistence.domain.Policy;
import com.jocoos.mybeautip.domain.policy.persistence.domain.PolicyHistory;
import com.jocoos.mybeautip.domain.policy.persistence.repository.PolicyHistoryRepository;
import com.jocoos.mybeautip.domain.policy.persistence.repository.PolicyRepository;
import com.jocoos.mybeautip.global.code.CountryCode;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PolicyDao {

    private final PolicyRepository repository;
    private final PolicyHistoryRepository historyRepository;

    @Transactional(readOnly = true)
    public Policy get(CountryCode countryCode) {
        return repository.findById(countryCode)
                .orElseThrow(() -> new NotFoundException("not found policy. id - " + countryCode));
    }

    @Transactional
    public Policy edit(CountryCode countryCode, EditPolicyRequest request) {
        Policy policy = this.get(countryCode);
        PolicyHistory history = new PolicyHistory(policy, request);
        historyRepository.save(history);
        return policy.edit(request);
    }
}
