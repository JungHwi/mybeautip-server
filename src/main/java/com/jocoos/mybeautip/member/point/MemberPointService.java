package com.jocoos.mybeautip.member.point;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.admin.Dates;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.order.Order;

@Slf4j
@Service
public class MemberPointService {

  @Value("${mybeautip.point.earn-ratio}")
  private int pointRatio;

  @Value("${mybeautip.point.remind-expiring-point}")
  private int reminder;

  private final MemberRepository memberRepository;
  private final MemberPointRepository memberPointRepository;

  public MemberPointService(MemberRepository memberRepository,
                            MemberPointRepository memberPointRepository) {
    this.memberRepository = memberRepository;
    this.memberPointRepository = memberPointRepository;
  }

  public int getExpectedPoint(Member member) {
    AtomicInteger sum = new AtomicInteger();
    memberPointRepository.findByMemberAndState(member, MemberPoint.STATE_WILL_BE_EARNED).stream().forEach(p -> {
      sum.addAndGet(p.getPoint());
    });

    log.debug("points to be expected: {}", sum.get());
    return sum.get();
  }

  public void earnPoints(Order order) {
    if (order.getCreatedBy() == null) {
      return;
    }

    log.debug("member id: {}, order id: {}, earned point: {}", order.getCreatedBy().getId(), order.getId(), order.getExpectedPoint());

    MemberPoint memberPoint = new MemberPoint(order.getCreatedBy(), order, order.getExpectedPoint());
    memberPointRepository.save(memberPoint);
  }

  public void usePoints(Order order, int point) {
    if (order.getCreatedBy() == null) {
      return;
    }

    log.debug("member id: {}, order id: {}, use point: {}", order.getCreatedBy().getId(), order.getId(), point);

    MemberPoint memberPoint = new MemberPoint(order.getCreatedBy(), order, point, MemberPoint.STATE_USE_POINT);
    memberPointRepository.save(memberPoint);
  }

  // Using from confirmOrder in AdminBatchController
  @Transactional
  public void convertPoint(MemberPoint memberPoint) {
    if (memberPoint == null) {
      return;
    }

    Date now = new Date();
    Date expiry = Dates.afterMonths(now, 12);

    memberPoint.setEarnedAt(now);
    memberPoint.setExpiryAt(expiry);
    memberPoint.setState(MemberPoint.STATE_EARNED_POINT);
    memberPointRepository.save(memberPoint);

    Member member = memberPoint.getMember();
    member.setPoint(member.getPoint() + memberPoint.getPoint());
    memberRepository.save(member);
  }

  @Transactional
  public MemberPoint presentPoint(Long memberId, int point, Date expiryAt) {

    if (point <= 0) {
      throw new BadRequestException("The point must be grater than 0");
    }

    Member m = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException());
    m.setPoint(m.getPoint() + point);
    memberRepository.save(m);

    boolean remind = false;
    if (Date.from(Instant.now().plus(reminder, ChronoUnit.DAYS)).after(expiryAt)) {
      // no need to remind
      remind = true;
    }
    return memberPointRepository.save(createPresentPoint(m, point, expiryAt, remind));
  }

  private MemberPoint createPresentPoint(Member member, int point, Date expiryAt, boolean remind) {
    return new MemberPoint(member, null, point, MemberPoint.STATE_PRESENT_POINT, expiryAt, remind);
  }

  @Transactional
  public MemberPoint expiredPoint(MemberPoint memberPoint, Date expiredAt) {
    if (memberPoint == null) {
      return null;
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
    return memberPoint;
  }
}
