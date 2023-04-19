package com.jocoos.mybeautip.domain.company.service;

import com.jocoos.mybeautip.domain.company.converter.CompanyConverter;
import com.jocoos.mybeautip.domain.company.dto.CompanyResponse;
import com.jocoos.mybeautip.domain.company.dto.CreateCompanyRequest;
import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import com.jocoos.mybeautip.domain.company.service.dao.CompanyDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CompanyService {

    private final CompanyDao dao;
    private final CompanyConverter converter;

    public CompanyResponse create(CreateCompanyRequest request) {
        Company company = dao.create(request);
        return converter.converts(company);
    }
}
