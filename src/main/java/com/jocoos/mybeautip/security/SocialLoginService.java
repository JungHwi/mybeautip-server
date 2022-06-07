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
        SocialMemberRequest socialMemberRequest = loadMember(provider, code, state);

        try {
            Member member = saveOrUpdate(socialMemberRequest);
            AccessTokenResponse accessTokenResponse = jwtTokenProvider.auth(member);
            response = new WebSocialLoginResponse(accessTokenResponse);
        } catch (MemberNotFoundException ex) {
            response = new WebSocialLoginResponse(socialMemberRequest);
        }

        return response;
    }

    @Transactional
    public SocialMemberRequest loadMember(String provider, String code, String state) {
        LoginService loginService = loginServiceFactory.getLoginService(provider);

        SocialMemberRequest socialMemberRequest = loginService.getMember(code, state);
        log.debug("{}", socialMemberRequest);

        return socialMemberRequest;
    }

    private Member saveOrUpdate(SocialMemberRequest socialMemberRequest) {
        Member member = findMember(socialMemberRequest);

        return memberRepository.save(member);
    }

    private Member findMember(SocialMemberRequest socialMemberRequest) {
        Member member = null;
        Long memberId = null;

        switch (socialMemberRequest.getProvider()) {
            case KakaoLoginService.PROVIDER_TYPE:
                memberId = kakaoMemberRepository.findById(socialMemberRequest.getId())
                        .map(s -> s.getMemberId())
                        .orElseThrow(() -> new MemberNotFoundException("No such kakao member. kakao id - " + socialMemberRequest.getId()));
                break;
            case NaverLoginService.PROVIDER_TYPE:
                memberId = naverMemberRepository.findById(socialMemberRequest.getId())
                        .map(s -> s.getMemberId())
                        .orElseThrow(() -> new MemberNotFoundException("No such naver member. naver id - " + socialMemberRequest.getId()));
                break;
            case FacebookLoginService.PROVIDER_TYPE:
                memberId = facebookMemberRepository.findById(socialMemberRequest.getId())
                        .map(s -> s.getMemberId())
                        .orElseThrow(() -> new MemberNotFoundException("No such facebook member. facebook id - " + socialMemberRequest.getId()));
                break;
            default:
                throw new MybeautipRuntimeException("Unsupported provider type");
        }

        member = memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElse(socialMemberRequest.toMember());

        return member;
    }
}
