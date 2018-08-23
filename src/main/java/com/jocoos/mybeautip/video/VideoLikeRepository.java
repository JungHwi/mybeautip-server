package com.jocoos.mybeautip.video;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoLikeRepository extends JpaRepository<VideoLike, Long> {
  Optional<VideoLike> findByVideoIdAndCreatedBy(Long videoId, Long memberId);

  Optional<VideoLike> findByIdAndVideoIdAndCreatedBy(Long likeId, Long videoId, Long memberId);

  Slice<VideoLike> findByCreatedAtBeforeAndCreatedBy(Date createdAt, Long createdBy, Pageable pageable);

  Slice<VideoLike> findByCreatedBy(Long createdBy, Pageable pageable);

  Integer countByCreatedBy(Long memberId);
}