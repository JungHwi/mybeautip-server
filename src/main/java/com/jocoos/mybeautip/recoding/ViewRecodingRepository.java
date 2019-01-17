package com.jocoos.mybeautip.recoding;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jocoos.mybeautip.member.Member;

public interface ViewRecodingRepository extends JpaRepository<ViewRecoding, Long> {

  Slice<ViewRecoding> findByCreatedByIdAndModifiedAtBeforeAndModifiedAtAfter(Long memberId, Date now, Date weekAgo, Pageable pageable);

  Slice<ViewRecoding> findByCategoryAndCreatedByIdAndModifiedAtBeforeAndModifiedAtAfter(Integer category, Long memberId, Date now, Date weekAgo, Pageable pageable);
  
  Optional<ViewRecoding> findByItemIdAndCategoryAndCreatedBy(String itemId, int category, Member createdBy);
}