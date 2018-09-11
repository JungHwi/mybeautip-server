package com.jocoos.mybeautip.member.point;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.member.Member;

@Slf4j
@Service
public class PointService {

  @Value("${mybeautip.point.earn-ratio}")
  private int pointRatio;

  private final MemberPointRepository memberPointRepository;

  public PointService(MemberPointRepository memberPointRepository) {
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

  public void earnPoints(Member member, Long price) {
    if (member == null) {
      return;
    }

    int point = Math.toIntExact(((price * pointRatio) / 100));
    log.debug("member id: {}, earned point: {}", member.getId(), point);

    MemberPoint memberPoint = new MemberPoint(member, point);
    memberPointRepository.save(memberPoint);
  }
}
