package com.jocoos.mybeautip.domain.delivery.service.dao;

import com.jocoos.mybeautip.domain.delivery.code.DeliveryCompanyStatus;
import com.jocoos.mybeautip.domain.delivery.dto.CreateDeliveryCompanyRequest;
import com.jocoos.mybeautip.domain.delivery.persistence.domain.DeliveryCompany;
import com.jocoos.mybeautip.domain.delivery.persistence.repository.DeliveryCompanyRepository;
import com.jocoos.mybeautip.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class DeliveryCompanyDao {

    private final DeliveryCompanyRepository repository;

    @Transactional
    public DeliveryCompany create(CreateDeliveryCompanyRequest request) {
        DeliveryCompany deliveryCompany = new DeliveryCompany(request);
        deliveryCompany = repository.save(deliveryCompany);
        deliveryCompany.generateCode();
        return deliveryCompany;
    }

    @Transactional(readOnly = true)
    public List<DeliveryCompany> search() {
        return repository.findByStatusIn(List.of(DeliveryCompanyStatus.ACTIVE, DeliveryCompanyStatus.INACTIVE));
    }

    @Transactional(readOnly = true)
    public DeliveryCompany get(long deliveryCompanyId) {
        return repository.findById(deliveryCompanyId)
                .orElseThrow(() -> new NotFoundException("Delivery Company not found. id - " + deliveryCompanyId));
    }
}
