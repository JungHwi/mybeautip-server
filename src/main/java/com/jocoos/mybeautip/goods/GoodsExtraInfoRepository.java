package com.jocoos.mybeautip.goods;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoodsExtraInfoRepository extends JpaRepository<GoodsExtraInfo, Long> {
  Optional<GoodsExtraInfo> findByGoodsNo(String goodsNo);
}
