package com.jocoos.mybeautip.domain.company.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.company.persistence.domain.Company;

public interface CompanyRepository extends ExtendedQuerydslJpaRepository<Company, Long>, CompanyCustomRepository {


}
