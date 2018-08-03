package com.jocoos.mybeautip.post;

import java.util.Date;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

  Slice<PostComment> findByPostId(Long postId, Pageable pageable);

  Slice<PostComment> findByPostIdAndCreatedAtAfter(Long postId, Date createdAt, Pageable pageable);
}
