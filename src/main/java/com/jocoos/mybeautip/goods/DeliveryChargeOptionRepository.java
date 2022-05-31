package com.jocoos.mybeautip.goods;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryChargeOptionRepository extends JpaRepository<DeliveryChargeOption, Integer> {
    Optional<DeliveryChargeOption> findByDeliveryChargeId(Integer deliveryChargeId);
}
