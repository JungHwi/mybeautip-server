package com.jocoos.mybeautip.search;

import com.jocoos.mybeautip.member.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
  Optional<SearchHistory> findByKeywordAndCreatedBy(String keyword, Member createdBy);
  
  List<SearchHistory> findByCreatedBy(Member createdBy, Pageable pageable);
  
  List<SearchHistory> findByCreatedByAndKeywordStartingWith(Member member, String keyword, Pageable pageable);
  List<SearchHistory> findByCreatedByAndKeywordContaining(Member member, String keyword, Pageable pageable);
  
  List<SearchHistory> findByKeywordStartingWith(String keyword, Pageable pageable);
  List<SearchHistory> findByKeywordContaining(String keyword, Pageable pageable);
}