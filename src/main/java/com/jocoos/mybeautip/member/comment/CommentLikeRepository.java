package com.jocoos.mybeautip.member.comment;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
  Optional<CommentLike> findByCommentIdAndCreatedById(Long commentId, Long createdBy);

  Optional<CommentLike> findByIdAndCommentIdAndCreatedById(Long id, Long commentId, Long createdBy);

  List<CommentLike> findAllByCommentId(Long id);
}