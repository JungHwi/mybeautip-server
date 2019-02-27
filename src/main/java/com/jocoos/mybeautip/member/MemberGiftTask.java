package com.jocoos.mybeautip.member;

import com.jocoos.mybeautip.member.coupon.MemberCoupon;
import com.jocoos.mybeautip.member.coupon.MemberCouponRepository;
import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.member.point.MemberPointRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.transaction.Transactional;
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

  @Scheduled(fixedDelay = 5000)
  public void removeNotUsedCouponIsExpired() {
    List<MemberCoupon> expiredCoupons = memberCouponRepository.findByUsedAtIsNullAndCouponEndedAtBefore(new Date());
    if (!CollectionUtils.isEmpty(expiredCoupons)) {
      log.debug("expired coupons found: {}", expiredCoupons);
      expiredCoupons.stream().forEach(memberCoupon -> {
        memberCouponRepository.delete(memberCoupon);
      });
    }
  }

  @Scheduled(fixedDelay = 5000)
  public void removePoints() {
    Date yearAgo = getYears(-1);
    
    List<MemberPoint> expires = memberPointRepository.findByStateAndEarnedAtBeforeAndExpiredAtIsNull(MemberPoint.STATE_EARNED_POINT, yearAgo);
    if (!CollectionUtils.isEmpty(expires)) {
      log.debug("expired at : {}", dateFormat.format(yearAgo));
      log.debug("expired points found: {}", expires);
      expires.stream().forEach(memberPoint -> {
        expiredPoint(memberPoint, yearAgo);
      });
    }
  }

  @Transactional
  private void convertPoint(MemberPoint memberPoint) {
    if (memberPoint == null) {
      return;
    }

    memberPoint.setEarnedAt(new Date());
    memberPoint.setState(MemberPoint.STATE_EARNED_POINT);
    memberPointRepository.save(memberPoint);

    Member member = memberPoint.getMember();
    member.setPoint(member.getPoint() + memberPoint.getPoint());
    memberRepository.save(member);
  }

  @Transactional
  private void expiredPoint(MemberPoint memberPoint, Date expiredAt) {
    if (memberPoint == null) {
      return;
    }

    memberPoint.setExpiredAt(expiredAt);
    memberPointRepository.save(memberPoint);

    MemberPoint expiredPoint = new MemberPoint(memberPoint.getMember(), memberPoint.getPoint(), MemberPoint.STATE_EXPIRED_POINT);
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
