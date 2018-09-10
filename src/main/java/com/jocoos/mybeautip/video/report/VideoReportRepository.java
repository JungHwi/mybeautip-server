package com.jocoos.mybeautip.video.report;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoReportRepository extends JpaRepository<VideoReport, Long> {
  Optional<VideoReport> findByVideoIdAndCreatedById(Long videoId, Long createdById);
}