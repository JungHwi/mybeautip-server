package com.jocoos.mybeautip.recommendation;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.tag.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface KeywordRecommendationRepository extends JpaRepository<KeywordRecommendation, Long> {
  List<KeywordRecommendation> findByTagNameStartingWith(String keyword, Pageable pageable);
  List<KeywordRecommendation> findByTagNameContaining(String keyword, Pageable pageable);
  
  Optional<KeywordRecommendation> findByMember(Member member);
  Optional<KeywordRecommendation> findByTag(Tag tag);
}