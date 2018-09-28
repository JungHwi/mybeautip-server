package com.jocoos.mybeautip.recoding;

import java.util.Date;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewRecodingRepository extends JpaRepository<ViewRecoding, Long> {

  Slice<ViewRecoding> findByCreatedByIdAndCreatedAtBeforeAndCreatedAtAfter(Long memberId, Date now, Date weekAgo, Pageable pageable);

  Slice<ViewRecoding> findByCategoryAndCreatedByIdAndCreatedAtBeforeAndCreatedAtAfter(Integer category, Long memberId, Date now, Date weekAgo, Pageable pageable);
}