package com.jocoos.mybeautip.video;

import com.jocoos.mybeautip.goods.Goods;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface VideoGoodsRepository extends JpaRepository<VideoGoods, Long> {

    Slice<VideoGoods> findByCreatedAtBeforeAndGoodsGoodsNoAndVideoVisibilityAndVideoDeletedAtIsNullAndVideoStateNot(
            Date cursor, String goodsNo, String visibility, String state, Pageable pageable);

    List<VideoGoods> findAllByVideoId(Long id);

    List<VideoGoods> findAllByMemberId(Long memberId);

    int countByGoodsGoodsNoAndVideoVisibilityAndVideoDeletedAtIsNullAndVideoStateNot(String goodsNo, String visibility, String state);

    List<VideoGoods> findByGoodsAndVideoVisibilityAndVideoDeletedAtIsNullAndVideoStateNot(Goods goods, String visibility, String state);

    void deleteByVideoId(Long id);
}