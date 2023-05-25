package com.jocoos.mybeautip.domain.delivery.service;

import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import com.jocoos.mybeautip.domain.company.service.dao.CompanyDao;
import com.jocoos.mybeautip.domain.delivery.converter.DeliveryFeePolicyConverter;
import com.jocoos.mybeautip.domain.delivery.dto.*;
import com.jocoos.mybeautip.domain.delivery.persistence.domain.DeliveryFeePolicy;
import com.jocoos.mybeautip.domain.delivery.service.dao.DeliveryFeePolicyDao;
import com.jocoos.mybeautip.domain.delivery.vo.DeliveryFeePolicySearchResult;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    @Transactional(readOnly = true)
    public DeliveryFeePolicyResponse get(long deliveryFeeId) {
        DeliveryFeePolicy deliveryFeePolicy = dao.get(deliveryFeeId);
        return converter.converts(deliveryFeePolicy);
    }

    @Transactional
    public DeliveryFeePolicyResponse edit(long deliveryFeeId, EditDeliveryFeePolicyRequest request) {
        DeliveryFeePolicy deliveryFeePolicy = dao.get(deliveryFeeId);
        deliveryFeePolicy.edit(request);
        return converter.converts(deliveryFeePolicy);
    }

    @Transactional
    public void patchDefault(long deliveryFeeId) {
        DeliveryFeePolicy deliveryFeePolicy = dao.get(deliveryFeeId);
        dao.initializeDefault(deliveryFeePolicy.getCompany());
        deliveryFeePolicy.setDefault();
    }

    @Transactional
    public void delete(Collection<Long> deliveryFeeIds) {
        dao.delete(deliveryFeeIds);
        updateDeliveryFeeToDefaultFee(deliveryFeeIds);
    }

    private void updateDeliveryFeeToDefaultFee(Collection<Long> deliveryFeeIds) {
        List<DeliveryFeePolicy> deletedList = dao.findByIds(deliveryFeeIds);
        Map<Company, List<DeliveryFeePolicy>> deletedMap = deletedList.stream()
                .collect(Collectors.groupingBy(DeliveryFeePolicy::getCompany));

        List<DeliveryFeePolicy> defaultFeeList = dao.getDefaultByCompany(deletedMap.keySet());
        Map<Company, DeliveryFeePolicy> defaultFeeMap = defaultFeeList.stream()
                .collect(Collectors.toMap(DeliveryFeePolicy::getCompany, Function.identity()));

        for (Map.Entry<Company, List<DeliveryFeePolicy>> entry : deletedMap.entrySet()) {
            List<Long> targetDeliveryFeeIds = entry.getValue().stream()
                    .map(DeliveryFeePolicy::getId)
                    .collect(Collectors.toList());

            Long defaultFeeId = defaultFeeMap.get(entry.getKey()).getId();
            // TODO @재훈님 대상이 되는 ID(targetDeliveryFeeIds)와 해당 공급사의 기본 배송비 ID(defaultFeeId) 는 구했습니다. 일괄로 변경하는 메소드 만들어서 주석 제거 해주세요.
            // storeService.changeDeliveryFee(targetDeliveryFeeIds, defaultFeeId);
            // preOrderService.changeDeliveryFee(targetDeliveryFeeIds, defaultFeeId);
        }

    }
}
