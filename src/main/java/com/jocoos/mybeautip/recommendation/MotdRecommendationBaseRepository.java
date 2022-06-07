package com.jocoos.mybeautip.recommendation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.Optional;

public interface MotdRecommendationBaseRepository extends JpaRepository<MotdRecommendationBase, Long> {

    Page<MotdRecommendationBase> findByBaseDateBefore(Date baseDate, Pageable pageable);

    Optional<MotdRecommendationBase> findByBaseDate(Date baseDate);

    @Modifying
    @Query("update MotdRecommendationBase m set m.motdCount = m.motdCount + ?2 where m.id = ?1")
    void updateMotdCount(Long id, int count);
}
