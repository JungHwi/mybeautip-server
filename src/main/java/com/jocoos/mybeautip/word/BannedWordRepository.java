package com.jocoos.mybeautip.word;

import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannedWordRepository extends JpaRepository<BannedWord, String> {
}
