package com.jocoos.mybeautip.domain.policy.persistence.repository;

import com.jocoos.mybeautip.domain.policy.persistence.domain.PolicyHistory;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyHistoryRepository extends DefaultJpaRepository<PolicyHistory, Long> {
}
