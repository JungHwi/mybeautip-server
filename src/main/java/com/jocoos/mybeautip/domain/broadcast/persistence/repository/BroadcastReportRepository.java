package com.jocoos.mybeautip.domain.broadcast.persistence.repository;

import com.jocoos.mybeautip.domain.broadcast.persistence.domain.BroadcastReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BroadcastReportRepository extends JpaRepository<BroadcastReport, Long> {
}
