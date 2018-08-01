package com.jocoos.mybeautip.post;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

  Optional<Post> findByIdAndDeletedAtIsNull(Long id);

  @Modifying
  @Query("update Post p set p.viewCount = p.viewCount + ?2, p.modifiedAt = now() where p.id = ?1")
  void updateViewCount(Long id, Long count);

  @Modifying
  @Query("update Post p set p.likeCount = p.likeCount + ?2, p.modifiedAt = now() where p.id = ?1")
  void updateLikeCount(Long id, Long count);


  Slice<Post> findByCategoryAndDeletedAtIsNull(int category, Pageable pageable);

  Slice<Post> findByCreatedAtBeforeAndDeletedAtIsNull(Date createdAt, Pageable pageable);

  Slice<Post> findByTitleContainingAndDeletedAtIsNull(String title, Pageable pageable);

  Slice<Post> findByCategoryAndCreatedAtBeforeAndDeletedAtIsNull(int category, Date createdAt, Pageable pageable);

  Slice<Post> findByCategoryAndTitleContainingAndDeletedAtIsNull(int category, String title, Pageable pageable);

  Slice<Post> findByCategoryAndTitleContainingAndCreatedAtBeforeAndDeletedAtIsNull(int category, String title, Date createdAt, Pageable pageable);

  Slice<Post> findByTitleContainingAndCreatedAtBeforeAndDeletedAtIsNull(String title, Date createdAt, Pageable pageable);
}
