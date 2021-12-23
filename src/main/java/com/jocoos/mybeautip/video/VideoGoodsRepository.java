package com.jocoos.mybeautip.video;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.member.Member;

public interface VideoGoodsRepository extends JpaRepository<VideoGoods, Long> {

 Slice<VideoGoods> findByCreatedAtBeforeAndGoodsGoodsNoAndVideoVisibilityAndVideoDeletedAtIsNullAndVideoStateNot(
     Date cursor, String goodsNo, String visibility, String state, Pageable pageable);
  
  List<VideoGoods> findAllByVideoId(Long id);
  
  List<VideoGoods> findAllByMemberId(Long memberId);

  int countByGoodsGoodsNoAndVideoVisibilityAndVideoDeletedAtIsNullAndVideoStateNot(String goodsNo, String visibility, String state);
  
  List<VideoGoods> findByGoodsAndVideoVisibilityAndVideoDeletedAtIsNullAndVideoStateNot(Goods goods, String visibility, String state);

  void deleteByVideoId(Long id);
}