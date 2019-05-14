package com.jocoos.mybeautip.member;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.member.coupon.MemberCoupon;
import com.jocoos.mybeautip.member.coupon.MemberCouponRepository;
import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.member.point.MemberPointRepository;
import com.jocoos.mybeautip.member.point.MemberPointService;
import com.jocoos.mybeautip.notification.NotificationService;

@Slf4j
@Component
public class MemberGiftTask {

  private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ");

  private final MemberRepository memberRepository;
  private final MemberCouponRepository memberCouponRepository;
  private final MemberPointRepository memberPointRepository;
  private final NotificationService notificationService;
  private final MemberPointService memberPointService;

  @Value("${mybeautip.point.earn-after-days}")
  private int earnedAfterDays;

  @Value("${mybeautip.point.remind-expiring-point}")
  private int reminder;

  public MemberGiftTask(MemberRepository memberRepository,
                        MemberCouponRepository memberCouponRepository,
                        MemberPointRepository memberPointRepository,
                        NotificationService notificationService,
                        MemberPointService memberPointService) {
    this.memberRepository = memberRepository;
    this.memberCouponRepository = memberCouponRepository;
    this.memberPointRepository = memberPointRepository;
    this.notificationService = notificationService;
    this.memberPointService = memberPointService;
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
        memberPointService.expiredPoint(memberPoint, now);
      });
    }
  }

  @Scheduled(fixedDelay = 60 * 1000)
  public void remindPoints() {
    Date before = getDays(reminder);
    List<MemberPoint> reminders = memberPointRepository.findByStateInAndExpiryAtBeforeAndRemindIsFalseAndExpiredAtIsNull(Lists.newArrayList(MemberPoint.STATE_EARNED_POINT, MemberPoint.STATE_PRESENT_POINT), before);
    if (!CollectionUtils.isEmpty(reminders)) {
      log.debug("reminder before 3 days : {}", dateFormat.format(reminder));
      log.debug("expired points found: {}", reminders);
      reminders.stream().forEach(memberPoint -> {
        notificationService.notifyReminderMemberPoint(memberPoint);
        memberPoint.setRemind(true);
        memberPointRepository.save(memberPoint);
      });
    }
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
