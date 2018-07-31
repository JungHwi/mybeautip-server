package com.jocoos.mybeautip.post;

import java.util.Date;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ViewedPostRepository extends JpaRepository<ViewedPost, Long> {

  Slice<ViewedPost> findByCreatedAtBeforeAndCreatedAtAfter(Date now, Date weekAgo, Pageable pageable);
}
