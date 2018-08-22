package com.jocoos.mybeautip.goods;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsOptionRepository extends JpaRepository<GoodsOption, Integer> {
  List<GoodsOption> findByGoodsNo(int goodsNo);
}