package com.jocoos.mybeautip.video;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoLikeRepository extends JpaRepository<VideoLike, Long> {
  Optional<VideoLike> findByVideoIdAndCreatedBy(Long videoId, Long memberId);

  Optional<VideoLike> findByIdAndVideoIdAndCreatedBy(Long likeId, Long videoId, Long memberId);
}