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

    Slice<GoodsRecommendation> findByStartedAtBeforeAndEndedAtAfterAndGoodsStateLessThanEqual(Date statedAt, Date endedAt, int state, Pageable page);

    Page<GoodsRecommendation> findByOrderBySeqDesc(Pageable pageable);

    Page<GoodsRecommendation> findByOrderByGoodsHitCntDesc(Pageable pageable);

    Page<GoodsRecommendation> findByOrderByGoodsOrderCntDesc(Pageable pageable);

    Page<GoodsRecommendation> findByOrderByGoodsLikeCountDesc(Pageable pageable);

    Page<GoodsRecommendation> findByGoodsCateCdOrderBySeqDesc(String code, Pageable pageable);

    Page<GoodsRecommendation> findByGoodsCateCdOrderByGoodsHitCntDesc(String code, Pageable pageable);

    Page<GoodsRecommendation> findByGoodsCateCdOrderByGoodsOrderCntDesc(String code, Pageable pageable);

    Page<GoodsRecommendation> findByGoodsCateCdOrderByGoodsLikeCountDesc(String code, Pageable pageable);

    Page<GoodsRecommendation> findByGoodsStateOrderBySeqDesc(int state, Pageable pageable);

    Page<GoodsRecommendation> findByGoodsCateCdAndGoodsStateOrderBySeqDesc(String code, int state, Pageable pageable);

}