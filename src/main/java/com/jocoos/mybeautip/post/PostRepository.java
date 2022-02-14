package com.jocoos.mybeautip.post;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PostRepository extends JpaRepository<Post, Long> {

  Optional<Post> findByIdAndDeletedAtIsNull(Long id);

  Optional<Post> findByIdAndCreatedByIdAndDeletedAtIsNull(Long id, Long createdBy);

  Optional<Post> findByIdAndStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndDeletedAtIsNull(Long id, Date startedAt, Date endedAt);

  @Modifying
  @Query("update Post p set p.viewCount = p.viewCount + ?2, p.modifiedAt = now() where p.id = ?1")
  void updateViewCount(Long id, int count);

  @Modifying
  @Query("update Post p set p.likeCount = p.likeCount + ?2, p.modifiedAt = now() where p.id = ?1")
  void updateLikeCount(Long id, int count);

  @Modifying
  @Query("update Post p set p.commentCount = p.commentCount + ?2, p.modifiedAt = now() where p.id = ?1")
  void updateCommentCount(Long id, int count);

  Page<Post> findByStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndCategoryAndDeletedAtIsNull(Date startedAt, Date endedAt, int category, Pageable pageable);
  Slice<Post> findByStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndDeletedAtIsNull(Date startedAt, Date endedAt, Pageable pageable);
  Slice<Post> findByStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndCategoryAndDeletedAtIsNullAndTitleContainingOrDescriptionContaining(Date startedAt, Date endedAt, int category, String title, String description, Pageable pageable);

  Page<Post> findByStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndCategoryAndLabelIdAndDeletedAtIsNull(Date startedAt, Date endedAt, int category, int label, Pageable pageable);
  Slice<Post> findByStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndCategoryAndLabelIdAndDeletedAtIsNullAndTitleContainingOrDescriptionContaining(Date startedAt, Date endedAt, int category, int label, String title, String description, Pageable pageable);
  Page<Post> findByStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndLabelIdAndDeletedAtIsNull(Date startedAt, Date endedAt, int label, Pageable pageable);
  Slice<Post> findByStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndLabelIdAndDeletedAtIsNullAndTitleContainingOrDescriptionContaining(Date startedAt, Date endedAt, int label, String title, String description, Pageable pageable);

  @Query("select p from Post p where p.deletedAt is null " +
      "and p.opened is true and p.startedAt < :now and p.endedAt > :now " +
      "and p.category != 4 " +
      "and (p.title like concat('%',:keyword,'%') or p.description like concat('%',:keyword,'%')) " +
      "and p.createdAt < :cursor order by p.createdAt desc")
  Slice<Post> searchPost(@Param("keyword") String keyword, @Param("now") Date now, @Param("cursor") Date cursor, Pageable pageable);
  
  // apis for Admin
  Page<Post> findByCategoryAndDeletedAtIsNull(int category, Pageable pageable);

  Page<Post> findByDeletedAtIsNull(Pageable pageable);

  Page<Post> findByDeletedAtIsNotNull(Pageable pageable);

}
