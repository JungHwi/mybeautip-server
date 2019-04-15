package com.jocoos.mybeautip.recommendation;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MotdRecommendationRepository extends JpaRepository<MotdRecommendation, Long> {

  Optional<MotdRecommendation> findByVideoId(Long id);

  Slice<MotdRecommendation> findByVideoVisibilityAndVideoDeletedAtIsNullAndVideoStateNotAndCreatedAtBefore(String visibility, String state, Date createdAt, Pageable pageable);

  Slice<MotdRecommendation> findByBaseIdAndStartedAtBeforeAndEndedAtAfterAndVideoVisibilityAndVideoDeletedAtIsNull(Long baseId, Date statedAt, Date endedAt, String visibility, Pageable pageable);

  Page<MotdRecommendation> findByVideoTypeAndVideoDeletedAtIsNull(String type, Pageable pageable);

}