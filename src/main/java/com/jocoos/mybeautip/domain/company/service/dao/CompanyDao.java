package com.jocoos.mybeautip.domain.company.service.dao;

import com.jocoos.mybeautip.domain.brand.service.dao.BrandDao;
import com.jocoos.mybeautip.domain.company.code.CompanyStatus;
import com.jocoos.mybeautip.domain.company.dto.CompanySearchRequest;
import com.jocoos.mybeautip.domain.company.dto.CreateCompanyRequest;
import com.jocoos.mybeautip.domain.company.dto.EditCompanyRequest;
import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import com.jocoos.mybeautip.domain.company.persistence.repository.CompanyRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CompanyDao {

    private final BrandDao brandDao;
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

    @Transactional(readOnly = true)
    public Company get(long companyId) {
        return repository.findById(companyId)
                .orElseThrow(() -> new NotFoundException("Company Not Found. Id is " + companyId));
    }

    @Transactional
    public Company edit(long companyId, EditCompanyRequest request) {
        Company company = get(companyId);
        disableBrand(company, request.status());
        return repository.save(company.edit(request));
    }

    private void disableBrand(Company company, CompanyStatus status) {
        if (company.getStatus() == CompanyStatus.ACTIVE && status == CompanyStatus.INACTIVE) {
            brandDao.disable(company);
        }
    }
}
