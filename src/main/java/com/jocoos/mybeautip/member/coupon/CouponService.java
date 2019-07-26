package com.jocoos.mybeautip.member.coupon;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.MybeautipRuntimeException;
import com.jocoos.mybeautip.member.Member;

@Slf4j
@Service
public class CouponService {

  private final CouponRepository couponRepository;
  private final MemberCouponRepository memberCouponRepository;

  @Value("${mybeautip.coupon.welcome-usage-days}")
  private int welcomeCouponUsageDays;

  public CouponService(CouponRepository couponRepository,
                       MemberCouponRepository memberCouponRepository) {
    this.couponRepository = couponRepository;
    this.memberCouponRepository = memberCouponRepository;
  }

  public List<MemberCoupon> findMemberCouponsByMember(Member member) {
    Date now = new Date();
    return memberCouponRepository.findByMemberAndCreatedAtBeforeAndExpiryAtAfterAndUsedAtIsNull(member, now, now);
  }

  public int countByCoupons(Member member) {
    Date now = new Date();
    return memberCouponRepository.countByMemberAndCouponStartedAtBeforeAndCouponEndedAtAfterAndUsedAtIsNull(member, now, now);
  }

  public MemberCoupon sendWelcomeCoupon(Member member) {
    Date now = new Date();
    Coupon coupon = couponRepository.findByCategoryAndStartedAtBeforeAndEndedAtAfter(Coupon.CATEGORY_WELCOME_COUPON, now, now)
       .orElseThrow(() -> new MybeautipRuntimeException("coupon not found"));

    return memberCouponRepository.save(new MemberCoupon(member, coupon, welcomeCouponUsageDays));
  }
}
