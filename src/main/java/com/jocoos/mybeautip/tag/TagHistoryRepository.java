package com.jocoos.mybeautip.tag;

import com.jocoos.mybeautip.member.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagHistoryRepository extends JpaRepository<TagHistory, Long> {
  Optional<TagHistory> findByTagAndCategoryAndCreatedBy(String tag, int category, Member createdBy);
  
  List<TagHistory> findByCreatedBy(Member createdBy, Pageable pageable);
  
  List<TagHistory> findByCreatedByAndTagStartingWith(Member createdBy, String tag, Pageable pageable);
  List<TagHistory> findByCreatedByAndTagContaining(Member createdBy, String tag, Pageable pageable);
  
  List<TagHistory> findByTagStartingWith(String tag, Pageable pageable);
  List<TagHistory> findByTagContaining(String tag, Pageable pageable);
}