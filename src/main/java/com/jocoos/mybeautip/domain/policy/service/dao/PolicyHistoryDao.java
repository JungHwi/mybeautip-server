package com.jocoos.mybeautip.domain.policy.service.dao;

import com.jocoos.mybeautip.domain.policy.persistence.domain.PolicyHistory;
import com.jocoos.mybeautip.domain.policy.persistence.repository.PolicyHistoryRepository;
import com.jocoos.mybeautip.domain.policy.service.PolicyHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PolicyHistoryDao {

    private final PolicyHistoryRepository repository;

    public Page<PolicyHistory> search(Pageable pageable) {
        return repository.findBy(pageable);
    }
}
