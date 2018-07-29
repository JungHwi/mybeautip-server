package com.jocoos.mybeautip.recommendation;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRecommendationRepository extends JpaRepository<MemberRecommendation, Long> {
}