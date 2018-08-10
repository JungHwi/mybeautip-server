package com.jocoos.mybeautip.video;

import java.util.Optional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface VideoRepository extends CrudRepository<Video, Long> {
  @Modifying
  @Query("update Video v set v.commentCount = v.commentCount + ?2, v.modifiedAt = now() where v.videoKey = ?1")
  void updateCommentCount(Long videoKey, int count);

  Optional<Video> findByVideoKey(Long videoKey);

  void deleteByVideoKey(Long videoKey);
}