package com.jocoos.mybeautip.recoding;

import com.jocoos.mybeautip.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ViewRecodingRepository extends JpaRepository<ViewRecoding, Long> {

    Slice<ViewRecoding> findByCreatedByIdAndModifiedAtBeforeAndModifiedAtAfter(Long memberId, Date now, Date weekAgo, Pageable pageable);

    Slice<ViewRecoding> findByCategoryAndCreatedByIdAndModifiedAtBeforeAndModifiedAtAfter(Integer category, Long memberId, Date now, Date weekAgo, Pageable pageable);

    Optional<ViewRecoding> findByItemIdAndCategoryAndCreatedBy(String itemId, int category, Member createdBy);

    Page<ViewRecoding> findByItemIdAndCategoryAndCreatedAtLessThanEqual(String itemId, int category, Date createdAt, Pageable pageable);

    List<ViewRecoding> findByItemIdAndCategoryAndCreatedAtLessThanEqual(String itemId, int category, Date createdAt);
}