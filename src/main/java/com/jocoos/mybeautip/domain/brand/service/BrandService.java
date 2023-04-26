package com.jocoos.mybeautip.domain.brand.service;

import com.jocoos.mybeautip.domain.brand.converter.BrandConverter;
import com.jocoos.mybeautip.domain.brand.dto.BrandResponse;
import com.jocoos.mybeautip.domain.brand.dto.CreateBrandRequest;
import com.jocoos.mybeautip.domain.brand.persistence.domain.Brand;
import com.jocoos.mybeautip.domain.brand.service.dao.BrandDao;
import com.jocoos.mybeautip.domain.company.code.CompanyStatus;
import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import com.jocoos.mybeautip.domain.company.service.dao.CompanyDao;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jocoos.mybeautip.global.exception.ErrorCode.COMPANY_NOT_AVAILABLE;

@RequiredArgsConstructor
@Service
public class BrandService {

    private final BrandDao dao;
    private final CompanyDao companyDao;
    private final BrandConverter converter;

    @Transactional
    public BrandResponse create(CreateBrandRequest request) {
        Company company = companyDao.get(request.getCompanyId());
        validCreate(company);

        Brand brand = dao.create(request, company);
        return converter.converts(brand);
    }

    private void validCreate(Company company) {
        if (company.getStatus() != CompanyStatus.ACTIVE) {
            throw new BadRequestException(COMPANY_NOT_AVAILABLE);
        }
    }
}
