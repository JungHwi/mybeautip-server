package com.jocoos.mybeautip.post;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

  Optional<Post> findByIdAndDeletedAtIsNull(Long id);

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

  Slice<Post> findByStartedAtBeforeAndEndedAtAfterAndOpenedIsTrueAndCategoryNotAndDeletedAtIsNullAndTitleContainingOrDescriptionContaining(Date startedAt, Date endedAt, int category, String title, String description, Pageable pageable);


  // apis for Admin
  Page<Post> findByCategoryAndDeletedAtIsNull(int category, Pageable pageable);

  Page<Post> findByDeletedAtIsNull(Pageable pageable);

  Page<Post> findByDeletedAtIsNotNull(Pageable pageable);

}
