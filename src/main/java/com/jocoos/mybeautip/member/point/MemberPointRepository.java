package com.jocoos.mybeautip.member.point;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.order.Order;

public interface MemberPointRepository extends JpaRepository<MemberPoint, Long> {

  Slice<MemberPoint> findByMemberAndCreatedAtBefore(Member member, Date createdAt, Pageable page);

  List<MemberPoint> findByMemberAndState(Member member, int state);

  List<MemberPoint> findByStateAndCreatedAtBeforeAndEarnedAtIsNull(int state, Date createdAt);

  List<MemberPoint> findByStateInAndExpiryAtBeforeAndExpiredAtIsNull(List<Integer> states, Date earnedAt);

  List<MemberPoint> findByStateInAndExpiryAtBeforeAndRemindIsFalseAndExpiredAtIsNull(List<Integer> states, Date earnedAt);

  Page<MemberPoint> findByMemberId(Long memberId, Pageable pageable);

  Page<MemberPoint> findByMemberIdAndState(Long memberId, int state, Pageable pageable);
  
  Optional<MemberPoint> findByMemberAndOrderAndPointAndState(Member member, Order order, int point, int state);


  // admin api
  Page<MemberPoint> findByStateAndOrderIsNull(int state, Pageable pageable);
}
