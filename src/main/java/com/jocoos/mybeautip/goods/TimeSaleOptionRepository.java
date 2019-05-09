package com.jocoos.mybeautip.goods;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface TimeSaleOptionRepository extends JpaRepository<TimeSaleOption, Long> {
    List<TimeSaleOption> findByGoodsNoAndStartedAtBeforeAndEndedAtAfterAndBrokerAndDeletedAtIsNull(Integer goodsNo, Date startedAt, Date endedAt, Long memberId);
    List<TimeSaleOption> findByStartedAtBeforeAndEndedAtAfterAndBrokerAndDeletedAtIsNull(Date startedAt, Date endedAt, Long memberId);
}
