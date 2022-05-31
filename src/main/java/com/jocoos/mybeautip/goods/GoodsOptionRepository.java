package com.jocoos.mybeautip.goods;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GoodsOptionRepository extends JpaRepository<GoodsOption, Integer> {
    List<GoodsOption> findByGoodsNo(int goodsNo);

    Optional<GoodsOption> findByGoodsNoAndOptionNoAndOptionViewFl(Integer goodsNo, Integer optionNo, String optionViewFl);
}