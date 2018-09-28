package com.jocoos.mybeautip.member.revenue;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RevenueRepository extends JpaRepository<Revenue, Long> {

  List<Revenue> findByVideoId(Long videoId);

  List<Revenue> findByVideoMemberId(Long memberId);
}
