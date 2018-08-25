package com.jocoos.mybeautip.video;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoCommentLikeRepository extends JpaRepository<VideoCommentLike, Long> {

  Optional<VideoCommentLike> findByCommentIdAndCreatedById(Long commentId, Long createdBy);

  Optional<VideoCommentLike> findByIdAndCommentIdAndCreatedById(Long id, Long commentId, Long createdBy);

  List<VideoCommentLike> findAllByCommentId(Long id);
}