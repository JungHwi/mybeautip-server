package com.jocoos.mybeautip.video.watches;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface VideoWatchRepository extends JpaRepository<VideoWatch, Long> {

    Optional<VideoWatch> findByVideoIdAndCreatedById(Long videoId, Long createdById);

    Optional<VideoWatch> findByVideoIdAndUsername(Long videoId, String username);

    List<VideoWatch> findByVideoIdAndModifiedAtAfter(Long videoId, Date time);

    Integer countByVideoIdAndModifiedAtAfter(Long videoId, Date time);

    Integer countByVideoIdAndIsGuestIsTrueAndModifiedAtAfter(Long videoId, Date time);

    Slice<VideoWatch> findByVideoIdAndIsGuestIsFalseAndModifiedAtAfterAndCreatedByIdAfter(Long videoId, Date time, Long createdBy, Pageable pageable);
}