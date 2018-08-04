package com.jocoos.mybeautip.goods;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsLikeRepository extends JpaRepository<GoodsLike, Long> {
  Optional<GoodsLike> findByGoodsNoAndCreatedBy(String goodsNo, Long createdBy);
}