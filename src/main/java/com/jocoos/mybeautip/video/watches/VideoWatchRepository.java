package com.jocoos.mybeautip.video.watches;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoWatchRepository extends JpaRepository<VideoWatch, Long> {

  Optional<VideoWatch> findByVideoIdAndCreatedById(Long videoId, Long createdById);

  Optional<VideoWatch> findByVideoIdAndUsername(Long videoId, String username);

  Integer countByVideoIdAndModifiedAtAfter(Long videoId, Date time);

  Slice<VideoWatch> findByVideoIdAndIsGuestIsFalseAndModifiedAtAfterAndCreatedByIdAfter(Long videoId, Date time, Long createdBy, Pageable pageable);
}