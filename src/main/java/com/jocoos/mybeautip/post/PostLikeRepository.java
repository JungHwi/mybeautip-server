package com.jocoos.mybeautip.post;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

  Optional<PostLike> findByPostIdAndCreatedById(Long postId, Long createdBy);

  Optional<PostLike> findByIdAndPostIdAndCreatedById(Long id, Long postId, Long createdBy);

  Slice<PostLike> findByCreatedAtBeforeAndCreatedByIdAndPostDeletedAtIsNull(Date createdAt, Long createdBy, Pageable pageable);

  Slice<PostLike> findByCreatedByIdAndPostDeletedAtIsNull(Long createdBy, Pageable pageable);

  Integer countByCreatedByIdAndPostDeletedAtIsNull(Long createdBy);
}
