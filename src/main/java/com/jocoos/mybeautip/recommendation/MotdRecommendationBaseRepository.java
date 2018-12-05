package com.jocoos.mybeautip.recommendation;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MotdRecommendationBaseRepository extends JpaRepository<MotdRecommendationBase, Long> {

  Slice<MotdRecommendationBase> findByBaseDateBefore(Date baseDate, Pageable pageable);

  Optional<MotdRecommendationBase> findByBaseDate(Date baseDate);

  @Modifying
  @Query("update MotdRecommendationBase m set m.motdCount = m.motdCount + ?2 where m.id = ?1")
  void updateMotdCount(Long id, int count);
}
