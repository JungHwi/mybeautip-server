package com.jocoos.mybeautip.member.coupon;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jocoos.mybeautip.member.Member;

@Service
public class CouponService {

  private final MemberCouponRepository memberCouponRepository;

  public CouponService(MemberCouponRepository memberCouponRepository) {
    this.memberCouponRepository = memberCouponRepository;
  }

  public List<MemberCoupon> findMemberCouponsByMember(Member member) {
    Date now = new Date();
    return memberCouponRepository.findByMemberAndCouponStartedAtBeforeAndCouponEndedAtAfterAndUsedAtIsNull(member, now, now);
  }
}
