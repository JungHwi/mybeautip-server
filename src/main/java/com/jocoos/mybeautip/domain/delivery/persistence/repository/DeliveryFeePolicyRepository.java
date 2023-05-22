package com.jocoos.mybeautip.domain.delivery.persistence.repository;

import com.jocoos.mybeautip.domain.delivery.persistence.domain.DeliveryFeePolicy;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryFeePolicyRepository extends DefaultJpaRepository<DeliveryFeePolicy, Long> {

    boolean existsByCode(String code);

}
