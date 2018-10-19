package com.jocoos.mybeautip.goods;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryChargeAreaRepository extends JpaRepository<DeliveryChargeArea, Integer> {
  Optional<DeliveryChargeArea> findByArea(String area);
  
  Optional<DeliveryChargeArea> findByPart1AndPart2AndPart3AndPart4(String part1, String part2, String part3, String part4);
}