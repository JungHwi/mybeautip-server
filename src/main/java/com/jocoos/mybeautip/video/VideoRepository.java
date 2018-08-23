package com.jocoos.mybeautip.video;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface VideoRepository extends CrudRepository<Video, Long> {

  Optional<Video> findByIdAndDeletedAtIsNull(Long id);

  Optional<Video> findByIdAndMemberIdAndDeletedAtIsNull(Long id, Long memberId);

  Optional<Object> findByVideoKey(String videoKey);

  @Modifying
  @Query("update Video v set v.commentCount = v.commentCount + ?2, v.modifiedAt = now() where v.id = ?1")
  void updateCommentCount(Long id, int count);

  @Modifying
  @Query("update Video v set v.likeCount = v.likeCount + ?2, v.modifiedAt = now() where v.id = ?1")
  void updateLikeCount(Long id, int i);
}