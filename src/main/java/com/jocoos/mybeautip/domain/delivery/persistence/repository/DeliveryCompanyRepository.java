package com.jocoos.mybeautip.domain.delivery.persistence.repository;

import com.jocoos.mybeautip.domain.delivery.code.DeliveryCompanyStatus;
import com.jocoos.mybeautip.domain.delivery.persistence.domain.DeliveryCompany;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryCompanyRepository extends DefaultJpaRepository<DeliveryCompany, Long> {

    List<DeliveryCompany> findByStatusIn(List<DeliveryCompanyStatus> statuses);
}
