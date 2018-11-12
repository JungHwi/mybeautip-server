package com.jocoos.mybeautip.recommendation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface GoodsRecommendationRepository extends JpaRepository<GoodsRecommendation, Long> {
  Optional<GoodsRecommendation> findByGoodsNo(String goodsNo);

  List<GoodsRecommendation> findAllByGoodsGoodsNo(String goodsNo);

  Page<GoodsRecommendation> findByOrderByGoodsHitCntDesc(Pageable pageable);

  Page<GoodsRecommendation> findByOrderByGoodsOrderCntDesc(Pageable pageable);

  Page<GoodsRecommendation> findByOrderByGoodsLikeCountDesc(Pageable pageable);
  
  Slice<GoodsRecommendation> findByStartedAtBeforeAndEndedAtAfterAndGoodsStateLessThanEqual(Date statedAt, Date endedAt, int state, Pageable page);
}