package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastReportType;
import com.jocoos.mybeautip.domain.broadcast.dto.BroadcastReportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BroadcastReportCustomRepository {
    Page<BroadcastReportResponse> getList(Long broadcastId, BroadcastReportType type, Pageable pageable);
}
