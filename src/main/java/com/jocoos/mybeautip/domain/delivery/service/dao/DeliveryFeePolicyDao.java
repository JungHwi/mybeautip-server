package com.jocoos.mybeautip.domain.delivery.service.dao;

import com.jocoos.mybeautip.domain.company.persistence.domain.Company;
import com.jocoos.mybeautip.domain.delivery.dto.CreateDeliveryFeePolicyRequest;
import com.jocoos.mybeautip.domain.delivery.persistence.domain.DeliveryFeePolicy;
import com.jocoos.mybeautip.domain.delivery.persistence.repository.DeliveryFeePolicyRepository;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.support.RandomUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class DeliveryFeePolicyDao {

    private final DeliveryFeePolicyRepository repository;

    @Transactional
    public DeliveryFeePolicy create(Company company, CreateDeliveryFeePolicyRequest request) {
        request.setCode(generateCode());
        DeliveryFeePolicy deliveryFeePolicy = new DeliveryFeePolicy(company, request);

        return repository.save(deliveryFeePolicy);
    }

    private String generateCode() {
        for (int i = 0; i < 5; i++) {
            String code = String.format("%04d", RandomUtils.getRandom(1, 9999));
            if (!repository.existsByCode(code)) {
                return code;
            }
        }
        throw new BadRequestException("Failed delivery fee code.");
    }
}
