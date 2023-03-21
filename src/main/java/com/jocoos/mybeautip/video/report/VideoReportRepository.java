package com.jocoos.mybeautip.video.report;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import com.jocoos.mybeautip.member.block.Block;
import com.jocoos.mybeautip.member.block.BlockStatus;

public interface VideoReportRepository extends JpaRepository<VideoReport, Long> {
    Optional<VideoReport> findByVideoIdAndCreatedById(Long videoId, Long createdById);

    List<VideoReport> findByVideoIdInAndCreatedById(List<Long> targetIds, Long memberId);

    Page<VideoReport> findByVideoStateAndVideoDeletedAtIsNull(String state, Pageable pageable);

    Page<VideoReport> findByVideoStateAndVideoDeletedAtIsNotNull(String state, Pageable pageable);

    Page<VideoReport> findByVideoId(Long videoId, Pageable pageable);
}