package com.jocoos.mybeautip.domain.company.persistence.repository;

import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {

}
