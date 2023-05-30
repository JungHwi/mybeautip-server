package com.jocoos.mybeautip.domain.policy.persistence.repository;

import com.jocoos.mybeautip.domain.policy.persistence.domain.Policy;
import com.jocoos.mybeautip.global.code.CountryCode;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepository extends DefaultJpaRepository<Policy, CountryCode> {
}
