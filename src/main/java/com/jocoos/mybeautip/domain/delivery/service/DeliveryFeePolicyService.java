package com.jocoos.mybeautip.domain.delivery.service;

import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import com.jocoos.mybeautip.domain.company.service.dao.CompanyDao;
import com.jocoos.mybeautip.domain.delivery.converter.DeliveryFeePolicyConverter;
import com.jocoos.mybeautip.domain.delivery.dto.CreateDeliveryFeePolicyRequest;
import com.jocoos.mybeautip.domain.delivery.dto.DeliveryFeePolicyResponse;
import com.jocoos.mybeautip.domain.delivery.persistence.domain.DeliveryFeePolicy;
import com.jocoos.mybeautip.domain.delivery.service.dao.DeliveryFeePolicyDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
