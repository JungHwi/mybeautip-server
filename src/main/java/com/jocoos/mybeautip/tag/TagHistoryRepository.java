package com.jocoos.mybeautip.tag;

import com.jocoos.mybeautip.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagHistoryRepository extends JpaRepository<TagHistory, Long> {
  Optional<TagHistory> findByTagAndCreatedBy(String tag, Member createdBy);
}