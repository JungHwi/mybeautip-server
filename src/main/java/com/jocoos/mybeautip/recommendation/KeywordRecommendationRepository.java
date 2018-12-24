package com.jocoos.mybeautip.recommendation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordRecommendationRepository extends JpaRepository<KeywordRecommendation, Long> {
  List<KeywordRecommendation> findByTagNameStartingWith(String keyword, Pageable pageable);
  List<KeywordRecommendation> findByTagNameContaining(String keyword, Pageable pageable);
}