package com.jocoos.mybeautip.video.view;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface VideoViewRepository extends JpaRepository<VideoView, Long> {

    Optional<VideoView> findByVideoIdAndCreatedById(Long videoId, Long createdById);

    Optional<VideoView> findByVideoIdAndGuestName(Long videoId, String guestName);

    Slice<VideoView> findByVideoIdAndAndCreatedByIsNotNullAndModifiedAtBefore(Long videoId, Date time, Pageable pageable);

    int countByVideoIdAndCreatedByIsNull(Long id);

    int countByVideoIdAndGuestNameIsNotNull(Long id);
}