package com.jocoos.mybeautip.video;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.member.Member;

public interface VideoGoodsRepository extends JpaRepository<VideoGoods, Long> {

  Slice<VideoGoods> findByCreatedAtBeforeAndGoodsGoodsNo(Date cursor, String goodsNo, Pageable pageable);

  List<VideoGoods> findAllByVideoId(Long id);

  int countByGoodsGoodsNo(String goodsNo);

  @Query("select distinct v.member from VideoGoods v where v.member in (select v2.member from VideoGoods v2 where v2.goods=?1)")
  List<Member> getDistinctMembers(Goods goods);
}