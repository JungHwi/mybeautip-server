package com.jocoos.mybeautip.goods;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface TimeSaleRepository extends JpaRepository<TimeSale, Long> {
    @Query("select ts from TimeSale ts" +
            "  where ts.goodsNo=?1 and " +
            "        ts.startedAt < ?2 and ts.endedAt > ?3 and" +
            "        (ts.broker is null or ts.broker=?4) and" +
            "        ts.deletedAt is null" +
            "  order by ts.broker desc")
    Slice<TimeSale> getTopTimeSale(String goodsNo, Date startedAt, Date endedAt, Long memberId, Pageable pageable);

    @Query("select ts from TimeSale ts" +
            "  where ts.startedAt < ?1 and ts.endedAt > ?2 and" +
            "        (ts.broker is null or ts.broker=?3) and" +
            "        ts.deletedAt is null" +
            "  order by ts.broker")
    List<TimeSale> getTimeSaleList(Date startedAt, Date endedAt, Long memberId);
}
