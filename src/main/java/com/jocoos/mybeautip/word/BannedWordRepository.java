package com.jocoos.mybeautip.word;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BannedWordRepository extends JpaRepository<BannedWord, String> {

    List<BannedWord> findByCategory(int category);
}
