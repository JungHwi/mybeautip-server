package com.jocoos.mybeautip.video;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface VideoCommentRepository extends CrudRepository<VideoComment, Long> {

  @Modifying
  @Query("update VideoComment c set c.commentCount = c.commentCount + ?2, c.modifiedAt = now() where c.id = ?1")
  void updateCommentCount(Long id, int count);

  @Modifying
  @Query("update VideoComment c set c.likeCount = c.likeCount + ?2, c.modifiedAt = now() where c.id = ?1")
  void updateLikeCount(Long id, int count);

  Optional<VideoComment> findByIdAndVideoId(Long id, Long videoId);

  Optional<VideoComment> findByIdAndVideoIdAndCreatedById(Long id, Long videoId, Long createdBy);

  Slice<VideoComment> findByParentId(Long parentId, Pageable pageable);

  Slice<VideoComment> findByParentIdAndCreatedAtAfter(Long parentId, Date createdAt, Pageable pageable);

  Slice<VideoComment> findByVideoIdAndCreatedAtAfterAndParentIdIsNull(Long id, Date createdAt, Pageable pageable);

  Slice<VideoComment> findByVideoIdAndParentIdIsNull(Long id, Pageable pageable);
}