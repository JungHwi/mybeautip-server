package com.jocoos.mybeautip.domain.company.service.dao;

import com.jocoos.mybeautip.domain.company.dto.CompanySearchRequest;
import com.jocoos.mybeautip.domain.company.dto.CreateCompanyRequest;
import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import com.jocoos.mybeautip.domain.company.persistence.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CompanyDao {

    private final CompanyRepository repository;

    @Transactional
    public Company create(CreateCompanyRequest request) {
        Company company = new Company(request);
        return repository.save(company);
    }

    @Transactional(readOnly = true)
    public Page<Company> search(CompanySearchRequest request) {
        return repository.search(request);
    }
}
