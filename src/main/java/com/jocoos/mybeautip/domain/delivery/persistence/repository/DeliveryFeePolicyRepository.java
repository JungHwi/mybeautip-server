package com.jocoos.mybeautip.domain.delivery.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import com.jocoos.mybeautip.domain.delivery.persistence.domain.DeliveryFeePolicy;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DeliveryFeePolicyRepository extends ExtendedQuerydslJpaRepository<DeliveryFeePolicy, Long>, DeliveryFeePolicyCustomRepository {

    boolean existsByCode(String code);
    boolean existsByCompanyAndIsDefault(Company company, boolean isDefault);

    @Modifying
    @Query("UPDATE DeliveryFeePolicy deliveryFee SET deliveryFee.isDefault = false WHERE deliveryFee.company = :company AND deliveryFee.isDefault = true")
    void initializeDefault(@Param("company") Company company);

}
