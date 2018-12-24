package com.jocoos.mybeautip.search;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KeywordRepository extends JpaRepository<Keyword, Long> {
  Optional<Keyword> findByKeyword(String keyword);
}