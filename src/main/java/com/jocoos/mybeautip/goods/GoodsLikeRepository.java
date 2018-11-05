package com.jocoos.mybeautip.goods;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface GoodsLikeRepository extends JpaRepository<GoodsLike, Long> {
  
  Optional<GoodsLike> findByGoodsGoodsNoAndCreatedById(String goodsNo, Long createdBy);

  Optional<GoodsLike> findByIdAndGoodsGoodsNoAndCreatedById(Long id, String goodsNo, Long createdBy);

  Slice<GoodsLike> findByCreatedAtBeforeAndCreatedByIdAndGoodsDeletedAtIsNull(Date createdAt, Long createdBy, Pageable pageable);

  Slice<GoodsLike> findByCreatedByIdAndGoodsDeletedAtIsNull(Long createdBy, Pageable pageable);

  Integer countByCreatedByIdAndGoodsDeletedAtIsNull(Long createdBy);
}