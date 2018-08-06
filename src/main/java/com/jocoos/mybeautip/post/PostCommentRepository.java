package com.jocoos.mybeautip.post;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

  @Modifying
  @Query("update PostComment p set p.commentCount = p.commentCount + ?2, p.modifiedAt = now() where p.id = ?1")
  void updateCommentCount(Long id, int count);

  Optional<PostComment> findByIdAndPostIdAndCreatedBy(Long id, Long postId, Long createdBy);

  Slice<PostComment> findByPostIdAndParentIdIsNull(Long postId, Pageable pageable);

  Slice<PostComment> findByPostIdAndCreatedAtAfterAndParentIdIsNull(Long postId, Date createdAt, Pageable pageable);

  Slice<PostComment> findByParentId(Long parentId, Pageable pageable);

  Slice<PostComment> findByParentIdAndCreatedAtAfter(Long parentId, Date createdAt, Pageable pageable);
}
