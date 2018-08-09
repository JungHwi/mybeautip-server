package com.jocoos.mybeautip.video;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.CrudRepository;

public interface VideoGoodsRepository extends CrudRepository<VideoGoods, Long> {
  Optional<VideoGoods> findByVideoKeyAndGoodsGoodsNo(String videoGoodsKey, String goodsNo);

  List<VideoGoods> findAllByVideoKey(String videoKey);

  Slice<VideoGoods> findByCreatedAtBeforeAndGoodsGoodsNo(Date cursor, String goodsNo, Pageable pageable);
}