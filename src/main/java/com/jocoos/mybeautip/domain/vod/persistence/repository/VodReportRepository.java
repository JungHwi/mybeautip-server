package com.jocoos.mybeautip.domain.vod.persistence.repository;

import com.jocoos.mybeautip.domain.vod.persistence.domain.VodReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface VodReportRepository extends JpaRepository<VodReport, Long> {
    boolean existsByVodIdAndReporterId(long vodId, long reporterId);

    List<VodReport> findAllByVodIdIn(Set<Long> ids);
}
