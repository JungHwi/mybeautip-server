package com.jocoos.mybeautip.goods;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DeliveryChargeDetailRepository extends JpaRepository<DeliveryChargeDetail, Integer> {
    Optional<DeliveryChargeDetail> findByDeliveryChargeIdAndUnitStartLessThanEqualAndUnitEndGreaterThan(
            int deliveryChargeId, int start, int end);
}
