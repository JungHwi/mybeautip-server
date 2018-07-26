package com.jocoos.mybeautip.post;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

  Optional<Post> findByIdAndDeletedAtIsNull(Long id);

  @Modifying
  @Query("update Post p set p.viewCount = p.viewCount + ?2, p.modifiedAt = now() where p.id = ?1")
  void updateViewCount(Long id, Long count);
}
