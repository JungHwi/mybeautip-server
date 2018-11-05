package com.jocoos.mybeautip.video;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoLikeRepository extends JpaRepository<VideoLike, Long> {
  Optional<VideoLike> findByVideoIdAndCreatedById(Long videoId, Long memberId);

  Optional<VideoLike> findByIdAndVideoIdAndCreatedById(Long likeId, Long videoId, Long memberId);

  Slice<VideoLike> findByCreatedAtBeforeAndCreatedById(Date createdAt, Long createdBy, Pageable pageable);

  Slice<VideoLike> findByVideoIdAndCreatedAtBefore(Long videoId, Date createdAt, Pageable pageable);

  Slice<VideoLike> findByCreatedById(Long createdBy, Pageable pageable);

  Integer countByCreatedByIdAndVideoVisibilityAndVideoDeletedAtIsNull(Long memberId, String visibility);

  void deleteByVideoId(Long videoId);
}