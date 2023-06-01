package com.jocoos.mybeautip.domain.policy.persistence.repository;

import com.jocoos.mybeautip.domain.policy.persistence.domain.PolicyHistory;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyHistoryRepository extends DefaultJpaRepository<PolicyHistory, Long> {

    Page<PolicyHistory> findBy(Pageable pageable);
}
