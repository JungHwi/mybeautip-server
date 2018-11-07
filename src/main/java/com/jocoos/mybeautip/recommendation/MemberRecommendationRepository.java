package com.jocoos.mybeautip.recommendation;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface MemberRecommendationRepository extends JpaRepository<MemberRecommendation, Long> {
  List<MemberRecommendation> findByStartedAtBeforeAndEndedAtAfter(Date statedAt, Date endedAt, Pageable pageable);
}