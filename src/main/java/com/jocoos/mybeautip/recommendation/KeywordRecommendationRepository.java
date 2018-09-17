package com.jocoos.mybeautip.recommendation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface KeywordRecommendationRepository extends JpaRepository<KeywordRecommendation, Long> {
}