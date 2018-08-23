package com.jocoos.mybeautip.video;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.repository.CrudRepository;

public interface VideoGoodsRepository extends CrudRepository<VideoGoods, Long> {

  Slice<VideoGoods> findByCreatedAtBeforeAndGoodsGoodsNo(Date cursor, String goodsNo, Pageable pageable);

  void deleteByVideoId(Long id);

  List<VideoGoods> findAllByVideoId(Long id);

  int countByGoodsGoodsNo(String goodsNo);
}