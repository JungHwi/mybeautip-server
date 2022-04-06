package com.jocoos.mybeautip.security;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jocoos.mybeautip.exception.MybeautipRuntimeException;
import com.jocoos.mybeautip.member.*;

import org.apache.commons.lang3.StringUtils;
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
  public Member loadMember(String provider, String code, String state, String redirectUri) {
    log.debug("provider: {}, code: {}, redirect url: {}", provider, code, redirectUri);
    setProvider(provider);

    SocialMember member = loginService.getMember(code, state, redirectUri);
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
    Long memberId = null;

    switch (socialMember.getProvider()) {
      case KakaoLoginService.PROVIDER_TYPE:
        memberId = kakaoMemberRepository.findById(socialMember.getId())
            .map(s -> s.getMemberId())
            .orElse(null);
        break;
      case NaverLoginService.PROVIDER_TYPE:
        memberId = naverMemberRepository.findById(socialMember.getId())
            .map(s -> s.getMemberId())
            .orElse(null);
        break;

      case FacebookLoginService.PROVIDER_TYPE:
        memberId = facebookMemberRepository.findById(socialMember.getId())
            .map(s -> s.getMemberId())
            .orElse(null);
        break;
      default:
        throw new MybeautipRuntimeException("Unsupported provider type");
    }

    if (memberId != null) {
      member = memberRepository.findByIdAndDeletedAtIsNull(memberId)
          .orElse(socialMember.toMember());
    } else {
      member = socialMember.toMember();
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
