package com.jocoos.mybeautip.tag;

import com.jocoos.mybeautip.member.Member;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagHistoryRepository extends JpaRepository<TagHistory, Long> {
  Optional<TagHistory> findByTagAndCategoryAndResourceIdAndCreatedBy(Tag tag, int category, long resourceId, Member createdBy);
  
  List<TagHistory> findByCreatedBy(Member createdBy, Pageable pageable);
  List<TagHistory> findByCreatedBy(Member createdBy);
  
  List<TagHistory> findByCreatedByAndTagNameStartingWith(Member createdBy, String name, Pageable pageable);
  List<TagHistory> findByCreatedByAndTagNameContaining(Member createdBy, String name, Pageable pageable);
  
  List<TagHistory> findByTagNameStartingWith(String name, Pageable pageable);
  List<TagHistory> findByTagNameContaining(String name, Pageable pageable);
}