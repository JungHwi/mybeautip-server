package com.jocoos.mybeautip.video.scrap;

import com.jocoos.mybeautip.member.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface VideoScrapRepository extends JpaRepository<VideoScrap, Long> {

    boolean existsByVideoIdAndCreatedByIdAndStatus(Long videoId, Long member, ScrapStatus status);

    boolean existsByCreatedByAndStatus(Member member, ScrapStatus status);

    Optional<VideoScrap> findByVideoIdAndCreatedByIdAndStatus(Long videoId, Long member, ScrapStatus status);

    List<VideoScrap> findByCreatedByIdAndCreatedAtBeforeAndVideoVisibilityAndVideoDeletedAtIsNullAndStatus(Long createdId, Date createdAt, String visibility, Pageable pageable, ScrapStatus status);

    List<VideoScrap> findByCreatedByIdAndVideoVisibilityAndVideoDeletedAtIsNullAndStatus(Long createdId, String visibility, Pageable pageable, ScrapStatus status);

    Optional<VideoScrap> findByVideoIdAndCreatedById(Long videoId, Long memberId);
}
