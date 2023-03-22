package com.jocoos.mybeautip.domain.broadcast.service.dao;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastReportType;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastReportResponse;
import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastReport;
import com.jocoos.mybeautip.domain.broadcast.persistence.repository.BroadcastReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@RequiredArgsConstructor
@Service
public class BroadcastReportDao {

    private final BroadcastReportRepository repository;

    @Transactional
    public BroadcastReport save(BroadcastReport report) {
        return repository.save(report);
    }

    @Transactional(readOnly = true)
    public Page<BroadcastReportResponse> getList(Long broadcastId, BroadcastReportType type, Pageable pageable) {
        return repository.getList(broadcastId, type, pageable);
    }

    @Transactional(readOnly = true)
    public Long countReportedBroadcast(ZonedDateTime from) {
        return repository.countDistinctByCreatedAtAfter(from);
    }

    @Transactional(readOnly = true)
    public boolean exist(long broadcastId, BroadcastReportType type, long reporterId) {
        return repository.existsByBroadcastIdAndTypeAndReporterId(broadcastId, type, reporterId);
    }
}
