package com.jocoos.mybeautip.post;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
  Optional<PostLike> findByPostIdAndCreatedBy(Long postId, Long createdBy);
}
