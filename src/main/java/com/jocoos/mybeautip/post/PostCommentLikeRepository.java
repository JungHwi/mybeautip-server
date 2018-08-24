package com.jocoos.mybeautip.post;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentLikeRepository extends JpaRepository<PostCommentLike, Long> {

  Optional<PostCommentLike> findByCommentIdAndCreatedById(Long commentId, Long createdBy);

  Optional<PostCommentLike> findByIdAndCommentIdAndCreatedById(Long id, Long commentId, Long createdBy);

}
