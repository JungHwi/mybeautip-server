package com.jocoos.mybeautip.word;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BannedWordRepository extends JpaRepository<BannedWord, String> {

  List<BannedWord> findByCategory(int category);
}
