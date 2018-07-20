package com.jocoos.mybeautip.video;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface VideoGoodsRepository extends CrudRepository<VideoGoods, Long> {
  Optional<VideoGoods> findByVideoKeyAndGoodsNo(String videoGoodsKey, String goodsNo);

  List<VideoGoods> findAllByVideoKey(String videoKey);

  @Query("select v from VideoGoods v where v.goodsNo = :goodsNo and v.createdAt < :cursor " +
          "order by v.createdAt desc")
  Slice<VideoGoods> findByGoodsNo(@Param("goodsNo")String goodsNo,
                                  @Param("cursor") Date cursor, Pageable of);
}