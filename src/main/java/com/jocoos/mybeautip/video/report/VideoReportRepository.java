package com.jocoos.mybeautip.video.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface VideoReportRepository extends JpaRepository<VideoReport, Long> {
    Optional<VideoReport> findByVideoIdAndCreatedById(Long videoId, Long createdById);
    List<VideoReport> findByVideoIdInAndCreatedById(List<Long> targetIds, Long memberId);
    Page<VideoReport> findByVideoId(Long videoId, Pageable pageable);
}
