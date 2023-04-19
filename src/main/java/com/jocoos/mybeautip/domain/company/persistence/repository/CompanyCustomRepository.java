package com.jocoos.mybeautip.domain.company.persistence.repository;

import com.jocoos.mybeautip.domain.company.dto.CompanySearchRequest;
import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import org.springframework.data.domain.Page;

public interface CompanyCustomRepository {
    Page<Company> search(CompanySearchRequest request);
}
