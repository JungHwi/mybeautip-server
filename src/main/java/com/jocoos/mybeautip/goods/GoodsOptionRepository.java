package com.jocoos.mybeautip.goods;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface GoodsOptionRepository extends JpaRepository<GoodsOption, Integer> {
  List<GoodsOption> findByGoodsNo(int goodsNo);

  Optional<GoodsOption> findByGoodsNoAndOptionNo(Integer goodsNo, Integer optionNo);
}