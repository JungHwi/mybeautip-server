package com.jocoos.mybeautip.member.coupon;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.notification.NotificationService;

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

  @Async
  public MemberCoupon sendWelcomeCoupon(Member member) {
    Date now = new Date();
    Optional<Coupon> coupon = couponRepository.findByCategoryAndStartedAtBeforeAndEndedAtAfter(Coupon.CATEGORY_WELCOME_COUPON, now, now);
    log.info("coupon: {}", coupon);

    if (coupon.isPresent()) {
      return memberCouponRepository.save(new MemberCoupon(member, coupon.get(), welcomeCouponUsageDays));
    }

    return null;
  }

  @Async
  public MemberCoupon sendEventCoupon(Member member) {
    Date now = new Date();
    Optional<Coupon> coupon = couponRepository.findByCategoryAndStartedAtBeforeAndEndedAtAfter(Coupon.CATEGORY_EVENT_COUPON, now, now);
    log.info("coupon: {}", coupon);

    if (coupon.isPresent()) {
      return memberCouponRepository.save(new MemberCoupon(member, coupon.get()));
    }

    return null;
  }

  public MemberCoupon sendEventCoupon(Member member, Coupon coupon) {
    boolean exists = memberCouponRepository.existsByMemberIdAndCouponIdAndExpiryAtAfter(member.getId(), coupon.getId(), new Date());
    log.info("member: {}, coupon: {}, exists: {}", member.getId(), coupon.getId(), exists);
    if (!exists) {
      return memberCouponRepository.save(new MemberCoupon(member, coupon));
    }

    return null;
  }
}
