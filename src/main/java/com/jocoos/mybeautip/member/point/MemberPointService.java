package com.jocoos.mybeautip.member.point;


import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;

@Slf4j
@Service
public class MemberPointService {

  private final MemberRepository memberRepository;
  private final MemberPointRepository memberPointRepository;

  public MemberPointService(MemberRepository memberRepository,
                            MemberPointRepository memberPointRepository) {
    this.memberRepository = memberRepository;
    this.memberPointRepository = memberPointRepository;
  }

  @Transactional
  public MemberPoint presentPoint(Long memberId, int point, Date expired) {

    if (point <= 0) {
      throw new BadRequestException("The point must be grater than 0");
    }

    Member m = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException());
    m.setPoint(m.getPoint() + point);
    memberRepository.save(m);

    return memberPointRepository.save(createPresentPoint(m, point, expired));
  }


  private MemberPoint createPresentPoint(Member member, int point, Date expired) {
    return new MemberPoint(member, null, point, MemberPoint.STATE_PRESENT_POINT, expired);
  }
}
