package com.jocoos.mybeautip.goods;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryChargeDetailRepository extends JpaRepository<DeliveryChargeDetail, Integer> {
  Optional<DeliveryChargeDetail> findByDeliveryChargeIdAndUnitStartLessThanEqualAndUnitEndGreaterThan(
    int deliveryChargeId, int start, int end);
}
