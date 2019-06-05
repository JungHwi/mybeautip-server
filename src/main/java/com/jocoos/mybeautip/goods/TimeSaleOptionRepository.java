package com.jocoos.mybeautip.goods;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface TimeSaleOptionRepository extends JpaRepository<TimeSaleOption, Long> {
    @Query("select tso from TimeSaleOption tso" +
            "  where tso.goodsNo=?1 and tso.startedAt < ?2 and tso.endedAt > ?3 and" +
            "        (tso.broker is null or tso.broker=?4) and" +
            "        tso.deletedAt is null" +
            "  order by tso.broker")
    List<TimeSaleOption> getTimeSaleOptionByGoodsNo(Integer goodsNo, Date startedAt, Date endedAt, Long memberId);

    @Query("select tso from TimeSaleOption tso" +
            "  where tso.startedAt < ?1 and tso.endedAt > ?2 and" +
            "        (tso.broker is null or tso.broker=?3) and" +
            "        tso.deletedAt is null" +
            "  order by tso.broker")
    List<TimeSaleOption> getTimeSaleOptionList(Date startedAt, Date endedAt, Long memberId);

    List<TimeSaleOption> findByGoodsNoAndBroker(Integer goodsNo, Long memberId);
}
