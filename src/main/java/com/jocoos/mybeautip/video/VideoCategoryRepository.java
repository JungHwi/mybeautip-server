package com.jocoos.mybeautip.video;


import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoCategoryRepository extends JpaRepository<VideoCategory, Long> {

  void deleteByVideoId(Long videoId);
}
