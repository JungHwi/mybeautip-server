package com.jocoos.mybeautip.member.comment;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

  @Modifying
  @Query("update Comment c set c.commentCount = c.commentCount + ?2, c.modifiedAt = now() where c.id = ?1")
  void updateCommentCount(Long id, int count);

  @Modifying
  @Query("update Comment c set c.likeCount = c.likeCount + ?2, c.modifiedAt = now() where c.id = ?1")
  void updateLikeCount(Long id, int count);

  Optional<Comment> findByIdAndPostId(Long id, Long postId);

  Optional<Comment> findByIdAndPostIdAndCreatedById(Long id, Long postId, Long createdBy);

  Slice<Comment> findByPostIdAndParentIdIsNull(Long postId, Pageable pageable);

  Slice<Comment> findByPostIdAndIdGreaterThanEqualAndParentIdIsNull(Long postId, Long cursor, Pageable pageable);

  Optional<Comment> findByIdAndVideoId(Long id, Long videoId);

  Optional<Comment> findByIdAndVideoIdAndCreatedById(Long id, Long videoId, Long createdBy);

  Slice<Comment> findByVideoIdAndIdGreaterThanEqualAndParentIdIsNull(Long id, Long cursor, Pageable pageable);

  Slice<Comment> findByVideoIdAndParentIdIsNull(Long id, Pageable pageable);

  Slice<Comment> findByParentId(Long parentId, Pageable pageable);

  Slice<Comment> findByParentIdAndIdGreaterThanEqual(Long parentId, Long cursor, Pageable pageable);

  Slice<Comment> findByCreatedByIdAndCreatedAtBeforeAndParentIdIsNull(Long id, Date createdAt, Pageable pageable);

  Slice<Comment> findByCreatedByIdAndParentIdIsNull(Long id, Pageable pageable);
}

