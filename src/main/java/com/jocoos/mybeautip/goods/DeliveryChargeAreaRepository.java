package com.jocoos.mybeautip.goods;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryChargeAreaRepository extends JpaRepository<DeliveryChargeArea, Integer> {
  Optional<DeliveryChargeArea> findByArea(String area);
}