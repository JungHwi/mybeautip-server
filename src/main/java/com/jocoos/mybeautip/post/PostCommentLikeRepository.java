package com.jocoos.mybeautip.post;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentLikeRepository extends JpaRepository<PostCommentLike, Long> {

  Optional<PostCommentLike> findByPostIdAndCreatedBy(Long postId, Long createdBy);

}
