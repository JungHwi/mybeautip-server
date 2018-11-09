package com.jocoos.mybeautip.goods;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface GoodsLikeRepository extends JpaRepository<GoodsLike, Long> {
  
  Optional<GoodsLike> findByGoodsGoodsNoAndCreatedByIdAndGoodsGoodsDisplayFlAndGoodsDeletedAtIsNull(String goodsNo, Long createdBy, String goodsDisplayFl);

  Optional<GoodsLike> findByIdAndGoodsGoodsNoAndCreatedByIdAndGoodsGoodsDisplayFlAndGoodsDeletedAtIsNull(Long id, String goodsNo, Long createdBy, String goodsDisplayFl);

  Slice<GoodsLike> findByCreatedAtBeforeAndCreatedByIdAndGoodsGoodsDisplayFlAndGoodsDeletedAtIsNull(Date createdAt, Long createdBy, String goodsDisplayFl, Pageable pageable);

  Slice<GoodsLike> findByCreatedByIdAndGoodsGoodsDisplayFlAndGoodsDeletedAtIsNull(Long createdBy, String goodsDisplayFl, Pageable pageable);

  Integer countByCreatedByIdAndGoodsGoodsDisplayFlAndGoodsDeletedAtIsNull(Long createdBy, String goodsDisplayFl);
}