package com.jocoos.mybeautip.video.scrap;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface VideoScrapRepository extends JpaRepository<VideoScrap, Long> {

  boolean existsByVideoIdAndCreatedById(Long videoId, Long member);

  Optional<VideoScrap> findByVideoIdAndCreatedById(Long videoId, Long member);

  List<VideoScrap> findByCreatedByIdAndCreatedAtBefore(Long createdId, Date createdAt, Pageable pageable);

  List<VideoScrap> findByCreatedById(Long createdId, Pageable pageable);
}
