package com.jocoos.mybeautip.goods;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TimeSaleRepository extends JpaRepository<TimeSale, Long> {
    Optional<TimeSale> findByGoodsNoAndStartedAtBeforeAndEndedAtAfterAndBrokerAndDeletedAtIsNull(String goodsNo, Date startedAt, Date endedAt, Long memberId);

    List<TimeSale> findByBrokerAndStartedAtBeforeAndEndedAtAfterAndDeletedAtIsNull(Long memberId, Date startedAt, Date endedAt);
}
