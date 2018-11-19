package com.jocoos.mybeautip.member.coupon;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jocoos.mybeautip.member.Member;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {

  List<MemberCoupon> findByMemberAndCouponStartedAtBeforeAndCouponEndedAtAfterAndUsedAtIsNull(Member member, Date statedAt, Date endedAt);

  int countByMemberAndCouponStartedAtBeforeAndCouponEndedAtAfterAndUsedAtIsNull(Member member, Date statedAt, Date endedAt);

  List<MemberCoupon> findByUsedAtIsNullAndCouponEndedAtBefore(Date date);
}
