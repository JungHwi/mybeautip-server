package com.jocoos.mybeautip.video;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface VideoLikeRepository extends JpaRepository<VideoLike, Long> {
    Optional<VideoLike> findByVideoIdAndCreatedById(Long videoId, Long memberId);

    Optional<VideoLike> findByIdAndVideoIdAndCreatedById(Long likeId, Long videoId, Long memberId);

    Slice<VideoLike> findByCreatedAtBeforeAndCreatedByIdAndVideoDeletedAtIsNull(Date createdAt, Long createdBy, Pageable pageable);

    Slice<VideoLike> findByVideoIdAndCreatedAtBeforeAndVideoDeletedAtIsNull(Long videoId, Date createdAt, Pageable pageable);

    Slice<VideoLike> findByCreatedByIdAndVideoDeletedAtIsNull(Long createdBy, Pageable pageable);

    Integer countByCreatedByIdAndVideoDeletedAtIsNull(Long memberId);

    void deleteByVideoId(Long videoId);
}