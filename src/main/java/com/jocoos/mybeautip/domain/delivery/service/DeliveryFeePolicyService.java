package com.jocoos.mybeautip.domain.delivery.service;

import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import com.jocoos.mybeautip.domain.company.service.dao.CompanyDao;
import com.jocoos.mybeautip.domain.delivery.converter.DeliveryFeePolicyConverter;
import com.jocoos.mybeautip.domain.delivery.dto.CreateDeliveryFeePolicyRequest;
import com.jocoos.mybeautip.domain.delivery.dto.DeliveryFeePolicyListResponse;
import com.jocoos.mybeautip.domain.delivery.dto.DeliveryFeePolicyResponse;
import com.jocoos.mybeautip.domain.delivery.dto.DeliveryFeePolicySearchRequest;
import com.jocoos.mybeautip.domain.delivery.persistence.domain.DeliveryFeePolicy;
import com.jocoos.mybeautip.domain.delivery.service.dao.DeliveryFeePolicyDao;
import com.jocoos.mybeautip.domain.delivery.vo.DeliveryFeePolicySearchResult;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DeliveryFeePolicyService {

    private final DeliveryFeePolicyDao dao;
    private final CompanyDao companyDao;
    private final DeliveryFeePolicyConverter converter;

    @Transactional
    public DeliveryFeePolicyResponse create(CreateDeliveryFeePolicyRequest request) {
        Company company = companyDao.get(request.getCompanyId());
        DeliveryFeePolicy deliveryFeePolicy = dao.create(company, request);
        return converter.converts(deliveryFeePolicy);
    }

    @Transactional(readOnly = true)
    public PageResponse<DeliveryFeePolicyListResponse> search(DeliveryFeePolicySearchRequest request) {
        Page<DeliveryFeePolicySearchResult> results = dao.search(request);
        List<DeliveryFeePolicyListResponse> contents = converter.converts(results.getContent());
        return new PageResponse<>(results.getTotalElements(), contents);
    }
}
