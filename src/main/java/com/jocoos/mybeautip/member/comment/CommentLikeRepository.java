package com.jocoos.mybeautip.member.comment;

import com.jocoos.mybeautip.global.code.LikeStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {
    Optional<CommentLike> findByCommentIdAndCreatedByIdAndStatus(Long commentId, Long createdBy, LikeStatus status);

    Optional<CommentLike> findByIdAndCommentIdAndCreatedById(Long id, Long commentId, Long createdBy);

    List<CommentLike> findAllByCommentId(Long id);

    Optional<CommentLike> findByCommentIdAndCreatedById(Long commentId, Long memberId);
}
