package com.jocoos.mybeautip.domain.company.service;

import com.jocoos.mybeautip.domain.company.converter.CompanyConverter;
import com.jocoos.mybeautip.domain.company.dto.CompanyListResponse;
import com.jocoos.mybeautip.domain.company.dto.CompanyResponse;
import com.jocoos.mybeautip.domain.company.dto.CompanySearchRequest;
import com.jocoos.mybeautip.domain.company.dto.CreateCompanyRequest;
import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import com.jocoos.mybeautip.domain.company.service.dao.CompanyDao;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CompanyService {

    private final CompanyDao dao;
    private final CompanyConverter converter;

    public CompanyResponse create(CreateCompanyRequest request) {
        Company company = dao.create(request);
        return converter.converts(company);
    }

    public PageResponse<CompanyListResponse> search(CompanySearchRequest request) {
        Page<Company> result = dao.search(request);
        List<CompanyListResponse> contents = converter.convertsToList(result.getContent());
        return new PageResponse<>(result.getTotalElements(), contents);
    }
}
