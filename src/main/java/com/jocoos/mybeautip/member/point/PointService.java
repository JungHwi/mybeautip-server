package com.jocoos.mybeautip.member.point;

import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.admin.Dates;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.order.Order;

@Slf4j
@Service
public class PointService {

  @Value("${mybeautip.point.earn-ratio}")
  private int pointRatio;

  private final MemberRepository memberRepository;
  private final MemberPointRepository memberPointRepository;

  public PointService(MemberRepository memberRepository,
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
    Date expired = Dates.afterMonths(now, 12);

    memberPoint.setEarnedAt(now);
    memberPoint.setExpiredAt(expired);
    memberPoint.setState(MemberPoint.STATE_EARNED_POINT);
    memberPointRepository.save(memberPoint);

    Member member = memberPoint.getMember();
    member.setPoint(member.getPoint() + memberPoint.getPoint());
    memberRepository.save(member);
  }
}
