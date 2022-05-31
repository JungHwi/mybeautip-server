package com.jocoos.mybeautip.recommendation;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface MemberRecommendationRepository extends JpaRepository<MemberRecommendation, Long> {
    List<MemberRecommendation> findByStartedAtBeforeAndEndedAtAfterAndMemberVisibleIsTrue(Date statedAt, Date endedAt, Pageable pageable);

    List<MemberRecommendation> findByStartedAtBeforeAndEndedAtAfterAndMemberVisibleIsTrueAndSeqGreaterThan(Date statedAt, Date endedAt, int seq, Pageable pageable);

    List<MemberRecommendation> findByStartedAtBeforeAndEndedAtAfterAndMemberVisibleIsTrueAndSeqLessThan(Date statedAt, Date endedAt, int seq, Pageable pageable);

    Optional<MemberRecommendation> findByMemberId(Long memberId);

    Page<MemberRecommendation> findByMemberDeletedAtIsNull(Pageable pageable);
}