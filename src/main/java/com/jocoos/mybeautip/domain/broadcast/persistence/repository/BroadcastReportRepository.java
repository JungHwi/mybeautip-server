package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.infobip.spring.data.jpa.ExtendedQuerydslJpaRepository;
import com.jocoos.mybeautip.domain.broadcast.code.BroadcastReportType;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastReport;

import java.time.ZonedDateTime;

public interface BroadcastReportRepository extends ExtendedQuerydslJpaRepository<BroadcastReport, Long>, BroadcastReportCustomRepository {
    long countDistinctByCreatedAtAfter(ZonedDateTime createdAt);

    boolean existsByBroadcastIdAndTypeAndReporterId(long broadcastId, BroadcastReportType type, long reporterId);
}
