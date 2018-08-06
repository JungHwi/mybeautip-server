package com.jocoos.mybeautip.post;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

  Optional<PostLike> findByPostIdAndCreatedBy(Long postId, Long createdBy);

  Optional<PostLike> findByIdAndPostIdAndCreatedBy(Long id, Long postId, Long createdBy);

  Slice<PostLike> findByCreatedAtBeforeAndCreatedBy(Date createdAt, Long createdBy, Pageable pageable);

  Slice<PostLike> findByCreatedBy(Long createdBy, Pageable pageable);
}
