package com.jocoos.mybeautip.video.scrap;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface VideoScrapRepository extends JpaRepository<VideoScrap, Long> {

  boolean existsByVideoIdAndCreatedById(Long videoId, Long member);

  Optional<VideoScrap> findByVideoIdAndCreatedById(Long videoId, Long member);

  List<VideoScrap> findByCreatedByIdAndCreatedAtBeforeAndVideoVisibilityAndVideoDeletedAtIsNull(Long createdId, Date createdAt, String visibility, Pageable pageable);

  List<VideoScrap> findByCreatedByIdAndVideoVisibilityAndVideoDeletedAtIsNull(Long createdId, String visibility, Pageable pageable);

}
