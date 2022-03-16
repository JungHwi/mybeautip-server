package com.jocoos.mybeautip.security;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jocoos.mybeautip.exception.MybeautipRuntimeException;
import com.jocoos.mybeautip.member.KakaoMember;
import com.jocoos.mybeautip.member.KakaoMemberRepository;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialLoginService {

  private final MemberRepository memberRepository;
  private final KakaoMemberRepository kakaoMemberRepository;
  private final KakaoLoginService kakaoLoginService;

  private LoginService loginService = null;

  @Transactional
  public Member loadMember(String provider, String code) {
    log.debug("provider: {}, code: {}", provider, code);
    setProvider(provider);

    SocialMember member = loginService.getMember(code);
    log.debug("{}", member);

    return saveOrUpdate(member);
  }

  private void setProvider(String provider) {
    switch (provider) {
      case "kakao":
        this.loginService = kakaoLoginService;
        break;
      default:
        throw new MybeautipRuntimeException("Unsupported provider type");
    }
  }

  private Member saveOrUpdate(SocialMember socialMember) {
    Member member = memberRepository.findByEmailAndDeletedAtIsNull(socialMember.getEmail())
        .orElse(socialMember.toMember());

    if (member.getId() == null) {

      member.setPushable(Boolean.TRUE);
      memberRepository.save(member);
      kakaoMemberRepository.save(new KakaoMember(socialMember.getId(), member.getId()));
      return member;
    }

    return memberRepository.save(member);
  }
}
