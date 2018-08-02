package com.jocoos.mybeautip.recoding;

import java.util.Date;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewRecodingRepository extends JpaRepository<ViewRecoding, Long> {

  Slice<ViewRecoding> findByCreatedAtBeforeAndCreatedAtAfter(Date now, Date weekAgo, Pageable pageable);
}
