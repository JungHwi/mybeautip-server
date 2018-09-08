package com.jocoos.mybeautip.video.view;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoViewRepository extends JpaRepository<VideoView, Long> {

  Optional<VideoView> findByVideoIdAndCreatedById(Long videoId, Long createdById);

  Slice<VideoView> findByVideoIdAndModifiedAtBefore(Long videoId, Date time, Pageable pageable);
}