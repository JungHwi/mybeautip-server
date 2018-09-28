package com.jocoos.mybeautip.member.revenue;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RevenueRepository extends JpaRepository<Revenue, Long> {

  List<Revenue> findByVideoId(Long videoId);

  List<Revenue> findByVideoMemberId(Long memberId);

  Slice<Revenue> findByVideoMemberId(Long memberId, Pageable pageable);

  Slice<Revenue> findByVideoMemberIdAndCreatedAtBefore(Long memberId, Date createdAt, Pageable pageable);
}
