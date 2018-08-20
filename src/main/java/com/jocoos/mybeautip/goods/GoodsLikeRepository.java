package com.jocoos.mybeautip.goods;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsLikeRepository extends JpaRepository<GoodsLike, Long> {
  Optional<GoodsLike> findByGoodsGoodsNoAndCreatedBy(String goodsNo, Long createdBy);

  Optional<GoodsLike> findByIdAndGoodsGoodsNoAndCreatedBy(Long id, String goodsNo, Long createdBy);

  Slice<GoodsLike> findByCreatedAtBeforeAndCreatedBy(Date createdAt, Long createdBy, Pageable pageable);

  Slice<GoodsLike> findByCreatedBy(Long createdBy, Pageable pageable);

  Integer countByCreatedBy(Long createdBy);

  List<GoodsLike> findAllByGoodsGoodsNo(String goodsNo);
}