package com.jocoos.mybeautip.member.point;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jocoos.mybeautip.member.Member;

public interface MemberPointRepository extends JpaRepository<MemberPoint, Long> {

  Slice<MemberPoint> findByMemberAndCreatedAtBefore(Member member, Date createdAt, Pageable page);

  List<MemberPoint> findByMemberAndState(Member member, int state);

  List<MemberPoint> findByStateAndCreatedAtBeforeAndEarnedAtIsNull(int state, Date createdAt);

  List<MemberPoint> findByStateAndEarnedAtBeforeAndExpiredAtIsNull(int state, Date earnedAt);
}
