package com.jocoos.mybeautip.search;

import com.jocoos.mybeautip.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
  Optional<SearchHistory> findByKeywordAndCreatedBy(String keyword, Member createdBy);
}