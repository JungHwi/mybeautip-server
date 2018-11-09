package com.jocoos.mybeautip.recommendation;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoodsRecommendationRepository extends JpaRepository<GoodsRecommendation, Long> {
  Optional<GoodsRecommendation> findByGoodsNoAndGoodsGoodsDisplayFlAndGoodsDeletedAtIsNull(String goodsNo, String goodsDisplayFl);
  
  Slice<GoodsRecommendation> findAllByGoodsGoodsDisplayFlAndGoodsDeletedAtIsNull(String goodsDisplayFl, Pageable page);
}