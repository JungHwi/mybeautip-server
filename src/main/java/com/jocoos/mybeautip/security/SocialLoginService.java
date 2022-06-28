package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import com.jocoos.mybeautip.global.exception.MybeautipException;
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
                memberId = getMemberIdForKakao(socialMemberRequest.getId());
                break;
            case NaverLoginService.PROVIDER_TYPE:
                memberId = getMemberIdForNaver(socialMemberRequest.getId());
                break;
            case FacebookLoginService.PROVIDER_TYPE:
                memberId = getMemberIdForFacebook(socialMemberRequest.getId());
                break;
            default:
                throw new MybeautipException("Unsupported provider type");
        }

        member = memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .orElse(socialMemberRequest.toMember());

        return member;
    }

    private long getMemberIdForKakao(String kakaoId) {
        KakaoMember kakaoMember = kakaoMemberRepository.findById(kakaoId)
                .orElseThrow(() -> new MemberNotFoundException("No such kakao member. kakao id - " + kakaoId));

        Member member = memberRepository.findById(kakaoMember.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("No such member. member id - " + kakaoMember.getMemberId()));

        switch (member.getStatus()) {
            case ACTIVE:
                return member.getId();
            case DORMANT:
                throw new MemberNotFoundException("Dormant Member. member id - " + kakaoMember.getMemberId());
            default:
                throw new MemberNotFoundException("No such member. member id - " + kakaoMember.getMemberId());
        }
    }

    private long getMemberIdForNaver(String naverId) {
        NaverMember naverMember = naverMemberRepository.findById(naverId)
                .orElseThrow(() -> new MemberNotFoundException("No such naver member. naver id - " + naverId));

        Member member = memberRepository.findById(naverMember.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("No such member. member id - " + naverMember.getMemberId()));

        switch (member.getStatus()) {
            case ACTIVE:
                return member.getId();
            case DORMANT:
                throw new MemberNotFoundException("Dormant Member. member id - " + naverMember.getMemberId());
            default:
                throw new MemberNotFoundException("No such member. member id - " + naverMember.getMemberId());
        }
    }

    private long getMemberIdForFacebook(String facebookId) {
        FacebookMember facebookMember = facebookMemberRepository.findById(facebookId)
                .orElseThrow(() -> new MemberNotFoundException("No such facebook member. facebook id - " + facebookId));

        Member member = memberRepository.findById(facebookMember.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("No such member. member id - " + facebookMember.getMemberId()));

        switch (member.getStatus()) {
            case ACTIVE:
                return member.getId();
            case DORMANT:
                throw new MemberNotFoundException("Dormant Member. member id - " + facebookMember.getMemberId());
            default:
                throw new MemberNotFoundException("No such member. member id - " + facebookMember.getMemberId());
        }
    }
}
