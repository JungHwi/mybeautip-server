package com.jocoos.mybeautip.search;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
  Optional<Keyword> findByKeyword(String keyword);
  
  @Modifying
  @Query("update Keyword k set k.count = k.count + ?2, k.modifiedAt = now() where k.id = ?1")
  void updateCount(Long id, int count);
}