package com.jocoos.mybeautip.member.comment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentIdAndCreatedById(Long commentId, Long createdBy);

    Optional<CommentLike> findByIdAndCommentIdAndCreatedById(Long id, Long commentId, Long createdBy);

    List<CommentLike> findAllByCommentId(Long id);

    boolean existsByCommentIdAndCreatedById(Long id, Long memberId);
}
