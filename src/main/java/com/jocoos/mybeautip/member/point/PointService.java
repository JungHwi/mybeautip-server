package com.jocoos.mybeautip.member.point;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.member.Member;

@Slf4j
@Service
public class PointService {

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
}
