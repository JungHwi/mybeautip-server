package com.jocoos.mybeautip.security;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import com.jocoos.mybeautip.exception.MybeautipRuntimeException;
import com.jocoos.mybeautip.member.*;

import com.google.common.base.Strings;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialLoginService {

  private final KakaoLoginService kakaoLoginService;
  private final NaverLoginService naverLoginService;
  private final FacebookLoginService facebookLoginService;

  private final MemberRepository memberRepository;
  private final KakaoMemberRepository kakaoMemberRepository;
  private final NaverMemberRepository naverMemberRepository;
  private final FacebookMemberRepository facebookMemberRepository;

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
      case KakaoLoginService.PROVIDER_TYPE:
        this.loginService = kakaoLoginService;
        break;
      case NaverLoginService.PROVIDER_TYPE:
        this.loginService = naverLoginService;
        break;
      case FacebookLoginService.PROVIDER_TYPE:
        this.loginService = facebookLoginService;
        break;
      default:
        throw new MybeautipRuntimeException("Unsupported provider type");
    }
  }

  private Member findMember(SocialMember socialMember) {
    Member member = null;
    /**
     * Kakao has empty email case
     */
    if (KakaoLoginService.PROVIDER_TYPE.equals(socialMember.getProvider()) &&
        Strings.isNullOrEmpty(socialMember.getEmail())) {
      Long memberId = kakaoMemberRepository.findById(socialMember.getId())
          .map(s -> s.getMemberId())
          .orElse(null);

      if (memberId != null) {
        member = memberRepository.findByIdAndDeletedAtIsNull(memberId)
            .orElse(socialMember.toMember());
      } else {
        member = socialMember.toMember();
      }
    } else {
      member = memberRepository.findByEmailAndDeletedAtIsNull(socialMember.getEmail())
          .orElse(socialMember.toMember());
    }
    return member;
  }

  private Member saveOrUpdate(SocialMember socialMember) {
    Member member = findMember(socialMember);

    if (member.getId() == null) {
      // FIXME: Default pushable agreement value is true?
      member.setPushable(Boolean.TRUE);
      memberRepository.save(member);

      log.debug("{}", socialMember);

      switch (socialMember.getProvider()) {
        case KakaoLoginService.PROVIDER_TYPE:
          kakaoMemberRepository.save(new KakaoMember(socialMember.getId(), member.getId()));
          break;
        case NaverLoginService.PROVIDER_TYPE:
          naverMemberRepository.save(new NaverMember(socialMember.getId(), socialMember.getName(), member.getId()));
          break;
        case FacebookLoginService.PROVIDER_TYPE:
          facebookMemberRepository.save(new FacebookMember(socialMember.getId(), member.getId()));
          break;
        default:
          throw new MybeautipRuntimeException("Unsupported provider type");
      }

      return member;
    }

    return memberRepository.save(member);
  }
}
