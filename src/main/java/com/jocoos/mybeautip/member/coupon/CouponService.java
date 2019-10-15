package com.jocoos.mybeautip.member.coupon;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

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
    return memberCouponRepository.countByMemberAndCreatedAtBeforeAndExpiryAtAfterAndUsedAtIsNull(member, now, now);
  }

  public MemberCoupon sendWelcomeCoupon(Member member) {
    Date now = new Date();
    Optional<Coupon> coupon = couponRepository.findByCategoryAndStartedAtBeforeAndEndedAtAfter(Coupon.CATEGORY_WELCOME_COUPON, now, now);
    log.info("coupon: {}", coupon);

    if (coupon.isPresent()) {
      return memberCouponRepository.save(new MemberCoupon(member, coupon.get(), welcomeCouponUsageDays));
    }

    return null;
  }

  public MemberCoupon sendEventCoupon(Member member) {
    Date now = new Date();
    Optional<Coupon> coupon = couponRepository.findByCategoryAndStartedAtBeforeAndEndedAtAfter(Coupon.CATEGORY_EVENT_COUPON, now, now);
    log.info("coupon: {}", coupon);

    if (coupon.isPresent()) {
      return sendCoupon(member, coupon.get());
    }

    return null;
  }

  public MemberCoupon sendCoupon(Member member, Coupon coupon) {
    if (!memberCouponRepository.existsByCouponId(coupon.getId())) {
      return memberCouponRepository.save(new MemberCoupon(member, coupon));
    } else {
      log.warn("member[{}] already has the coupon[{}]", member.getId(), coupon.getId());
    }

    return null;
  }
}
