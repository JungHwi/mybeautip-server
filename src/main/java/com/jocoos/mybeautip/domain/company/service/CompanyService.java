package com.jocoos.mybeautip.domain.company.service;

import com.jocoos.mybeautip.domain.company.converter.CompanyConverter;
import com.jocoos.mybeautip.domain.company.dto.*;
import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import com.jocoos.mybeautip.domain.company.service.dao.CompanyDao;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CompanyService {

    private final CompanyDao dao;
    private final CompanyConverter converter;

    @Transactional
    public CompanyResponse create(CreateCompanyRequest request) {
        Company company = dao.create(request);
        return converter.converts(company);
    }

    @Transactional(readOnly = true)
    public PageResponse<CompanyListResponse> search(CompanySearchRequest request) {
        Page<Company> result = dao.search(request);
        List<CompanyListResponse> contents = converter.convertsToList(result.getContent());
        return new PageResponse<>(result.getTotalElements(), contents);
    }

    @Transactional(readOnly = true)
    public CompanyResponse get(long companyId) {
        Company company = dao.get(companyId);
        return converter.converts(company);
    }

    @Transactional
    public CompanyResponse edit(long companyId, EditCompanyRequest request) {
        Company company = dao.edit(companyId, request);
        return converter.converts(company);
    }
}
