package com.jocoos.mybeautip.domain.delivery.service;

import com.jocoos.mybeautip.domain.delivery.converter.DeliveryCompanyConverter;
import com.jocoos.mybeautip.domain.delivery.dto.CreateDeliveryCompanyRequest;
import com.jocoos.mybeautip.domain.delivery.dto.DeliveryCompanyResponse;
import com.jocoos.mybeautip.domain.delivery.dto.EditDeliveryCompanyRequest;
import com.jocoos.mybeautip.domain.delivery.persistence.domain.DeliveryCompany;
import com.jocoos.mybeautip.domain.delivery.service.dao.DeliveryCompanyDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DeliveryCompanyService {

    private final DeliveryCompanyDao dao;
    private final DeliveryCompanyConverter converter;

    @Transactional
    public DeliveryCompanyResponse create(CreateDeliveryCompanyRequest request) {
        DeliveryCompany deliveryCompany = dao.create(request);
        return converter.converts(deliveryCompany);
    }

    @Transactional(readOnly = true)
    public List<DeliveryCompanyResponse> search() {
        List<DeliveryCompany> deliveryCompanyList = dao.search();
        return converter.converts(deliveryCompanyList);
    }

    @Transactional
    public DeliveryCompanyResponse edit(long deliveryCompanyId, EditDeliveryCompanyRequest request) {
        DeliveryCompany deliveryCompany = dao.get(deliveryCompanyId);
        deliveryCompany.edit(request);
        return converter.converts(deliveryCompany);
    }

    @Transactional
    public void delete(long deliveryCompanyId) {
        DeliveryCompany deliveryCompany = dao.get(deliveryCompanyId);
        deliveryCompany.delete();
    }

}
