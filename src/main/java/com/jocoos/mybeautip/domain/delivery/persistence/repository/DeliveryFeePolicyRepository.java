package com.jocoos.mybeautip.domain.delivery.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.delivery.persistence.domain.DeliveryFeePolicy;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryFeePolicyRepository extends ExtendedQuerydslJpaRepository<DeliveryFeePolicy, Long>, DeliveryFeePolicyCustomRepository {

    boolean existsByCode(String code);

}
