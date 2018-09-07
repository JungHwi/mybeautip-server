package com.jocoos.mybeautip.member.coupon;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jocoos.mybeautip.member.Member;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Long> {

  List<MemberCoupon> findByCreatedByAndCouponStartedAtBeforeAndCouponEndedAtAfter(Member member, Date statedAt, Date endedAt);
}
