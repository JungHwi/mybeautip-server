package com.jocoos.mybeautip.recommendation;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MotdRecommendationRepository extends JpaRepository<MotdRecommendation, Long> {
  Optional<MotdRecommendation> findByVideoKey(String videoKey);
}