package com.jocoos.mybeautip.search;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SearchStatRepository extends JpaRepository<SearchStat, Long> {
  Optional<SearchStat> findByKeyword(String keyword);
  
  @Modifying
  @Query("update SearchStat s set s.count = s.count + ?2, s.modifiedAt = now() where s.id = ?1")
  void updateCount(Long id, int i);
}