package com.jocoos.mybeautip.member;

import com.jocoos.mybeautip.member.coupon.MemberCoupon;
import com.jocoos.mybeautip.member.coupon.MemberCouponRepository;
import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.member.point.MemberPointRepository;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
public class MemberGiftTask {

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");

  private final MemberRepository memberRepository;
  private final MemberCouponRepository memberCouponRepository;
  private final MemberPointRepository memberPointRepository;

  @Value("${mybeautip.point.earn-after-days}")
  private int earnedAfterDays;

  public MemberGiftTask(MemberRepository memberRepository,
                        MemberCouponRepository memberCouponRepository,
                        MemberPointRepository memberPointRepository) {
    this.memberRepository = memberRepository;
    this.memberCouponRepository = memberCouponRepository;
    this.memberPointRepository = memberPointRepository;
  }

  @Scheduled(fixedDelay = 60 * 1000)
  public void removeNotUsedCouponIsExpired() {
    List<MemberCoupon> expiredCoupons = memberCouponRepository.findByUsedAtIsNullAndCouponEndedAtBefore(new Date());
    if (!CollectionUtils.isEmpty(expiredCoupons)) {
      log.debug("expired coupons found: {}", expiredCoupons);
      expiredCoupons.stream().forEach(memberCoupon -> {
        memberCouponRepository.delete(memberCoupon);
      });
    }
  }

  @Scheduled(fixedDelay = 60 * 1000)
  public void expirePoints() {
    Date now = new Date();
    List<MemberPoint> expires = memberPointRepository.findByStateInAndExpiryAtBeforeAndExpiredAtIsNull(Lists.newArrayList(MemberPoint.STATE_EARNED_POINT, MemberPoint.STATE_PRESENT_POINT), now);
    if (!CollectionUtils.isEmpty(expires)) {
      log.debug("expired at : {}", dateFormat.format(now));
      log.debug("expired points found: {}", expires);
      expires.stream().forEach(memberPoint -> {
        expiredPoint(memberPoint, now);
      });
    }
  }

  @Transactional
  private void expiredPoint(MemberPoint memberPoint, Date expiredAt) {
    if (memberPoint == null) {
      return;
    }

    memberPoint.setExpiredAt(expiredAt);
    memberPointRepository.save(memberPoint);

    MemberPoint expiredPoint = new MemberPoint(memberPoint.getMember(), null, memberPoint.getPoint(), MemberPoint.STATE_EXPIRED_POINT);
    expiredPoint.setCreatedAt(expiredAt);
    memberPointRepository.save(expiredPoint);

    Member member = memberPoint.getMember();
    if (memberPoint.getPoint() > member.getPoint()) {
      member.setPoint(0);
    } else {
      member.setPoint(member.getPoint() - memberPoint.getPoint());
    }

    memberRepository.save(member);
  }

  private Date getDays(int amount) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.DAY_OF_YEAR,amount);
    return calendar.getTime();
  }

  private Date getYears(int amount) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(new Date());
    calendar.add(Calendar.YEAR, amount);
    return calendar.getTime();
  }
}
