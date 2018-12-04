package com.jocoos.mybeautip.recommendation;

import java.util.Date;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MotdRecommendationBaseRepository extends JpaRepository<MotdRecommendationBase, Long> {

  Slice<MotdRecommendationBase> findByBaseDateBefore(Date baseDate, Pageable pageable);
}
