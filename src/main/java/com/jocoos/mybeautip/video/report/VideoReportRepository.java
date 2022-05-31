package com.jocoos.mybeautip.video.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoReportRepository extends JpaRepository<VideoReport, Long> {
    Optional<VideoReport> findByVideoIdAndCreatedById(Long videoId, Long createdById);

    Page<VideoReport> findByVideoStateAndVideoDeletedAtIsNull(String state, Pageable pageable);

    Page<VideoReport> findByVideoStateAndVideoDeletedAtIsNotNull(String state, Pageable pageable);

    Page<VideoReport> findByVideoId(Long videoId, Pageable pageable);
}