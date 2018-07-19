package com.jocoos.mybeautip.video;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface VideoRepository extends CrudRepository<Video, Long> {
  Optional<Video> findByVideoKeyAndGoodsNo(String videoKey, String goodsNo);

  List<Video> findAllByVideoKey(String videoKey);

  @Query("select v from Video v where v.goodsNo = :goodsNo and v.createdAt < :cursor order by v.createdAt desc")
  Slice<Video> findByGoodsNo(@Param("goodsNo")String goodsNo, @Param("cursor") Date cursor, Pageable of);
}