package com.jocoos.mybeautip.recommendation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoodsRecommendationRepository extends JpaRepository<GoodsRecommendation, Long> {
  Optional<GoodsRecommendation> findByGoodsNo(String goodsNo);
}