package com.jocoos.mybeautip.goods;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsLikeRepository extends JpaRepository<GoodsLike, Long> {
  Optional<GoodsLike> findByGoodsGoodsNoAndCreatedById(String goodsNo, Long createdBy);

  Optional<GoodsLike> findByIdAndGoodsGoodsNoAndCreatedById(Long id, String goodsNo, Long createdBy);

  Slice<GoodsLike> findByCreatedAtBeforeAndCreatedById(Date createdAt, Long createdBy, Pageable pageable);

  Slice<GoodsLike> findByCreatedById(Long createdBy, Pageable pageable);

  Integer countByCreatedById(Long createdBy);

  List<GoodsLike> findAllByGoodsGoodsNo(String goodsNo);
}