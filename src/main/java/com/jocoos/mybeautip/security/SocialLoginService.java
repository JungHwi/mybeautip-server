package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.MybeautipRuntimeException;
import com.jocoos.mybeautip.member.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SocialLoginService {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final KakaoMemberRepository kakaoMemberRepository;
    private final NaverMemberRepository naverMemberRepository;
    private final FacebookMemberRepository facebookMemberRepository;
    private final LoginServiceFactory loginServiceFactory;

    public WebSocialLoginResponse webSocialLogin(String provider, String code, String state) {
        WebSocialLoginResponse response;
        SocialMember socialMember = loadMember(provider, code, state);

        try {
            Member member = saveOrUpdate(socialMember);
            AccessTokenResponse accessTokenResponse = jwtTokenProvider.auth(member);
            response = new WebSocialLoginResponse(accessTokenResponse);
        } catch (MemberNotFoundException ex) {
            response = new WebSocialLoginResponse(socialMember);
        }

        return response;
    }

    @Transactional
    public SocialMember loadMember(String provider, String code, String state) {
        LoginService loginService = loginServiceFactory.getLoginService(provider);

        SocialMember socialMember = loginService.getMember(code, state);
        log.debug("{}", socialMember);

        return socialMember;
    }

    private Member saveOrUpdate(SocialMember socialMember) {
        Member member = findMember(socialMember);

        return memberRepository.save(member);
    }

    private Member findMember(SocialMember socialMember) {
        Member member = null;
        Long memberId = null;

        switch (socialMember.getProvider()) {
            case KakaoLoginService.PROVIDER_TYPE:
                memberId = kakaoMemberRepository.findById(socialMember.getId())
                        .map(s -> s.getMemberId())
                        .orElseThrow(() -> new MemberNotFoundException("No such kakao member. kakao id - " + socialMember.getId()));
                break;
            case NaverLoginService.PROVIDER_TYPE:
                memberId = naverMemberRepository.findById(socialMember.getId())
                        .map(s -> s.getMemberId())
                        .orElseThrow(() -> new MemberNotFoundException("No such naver member. naver id - " + socialMember.getId()));
                break;
            case FacebookLoginService.PROVIDER_TYPE:
                memberId = facebookMemberRepository.findById(socialMember.getId())
                        .map(s -> s.getMemberId())
                        .orElseThrow(() -> new MemberNotFoundException("No such facebook member. facebook id - " + socialMember.getId()));
                break;
            default:
                throw new MybeautipRuntimeException("Unsupported provider type");
        }

        member = memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElse(socialMember.toMember());

        return member;
    }
}
