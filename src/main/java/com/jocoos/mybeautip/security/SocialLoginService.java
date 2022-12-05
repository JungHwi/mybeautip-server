package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.domain.event.service.impl.SignupEventService;
import com.jocoos.mybeautip.domain.member.dto.ExceptionMemberResponse;
import com.jocoos.mybeautip.global.exception.*;
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
    private final AppleMemberRepository appleMemberRepository;
    private final LoginServiceFactory loginServiceFactory;
    private final SignupEventService signupEventService;

    public WebSocialLoginResponse webSocialLogin(String provider, String code, String state) {
        WebSocialLoginResponse response;
        SocialMemberRequest socialMemberRequest = loadMember(provider, code, state);

        try {
            Member member = saveOrUpdate(socialMemberRequest);
            AccessTokenResponse accessTokenResponse = jwtTokenProvider.auth(member);
            response = new WebSocialLoginResponse(accessTokenResponse);
            signupEventService.join(member);
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
        Member member = switch (socialMemberRequest.getProvider()) {
            case KakaoLoginService.PROVIDER_TYPE -> getMemberIdForKakao(socialMemberRequest.getId());
            case NaverLoginService.PROVIDER_TYPE -> getMemberIdForNaver(socialMemberRequest.getId());
            case FacebookLoginService.PROVIDER_TYPE -> getMemberIdForFacebook(socialMemberRequest.getId());
            case AppleLoginService.PROVIDER_TYPE -> getMemberIdForApple(socialMemberRequest.getId());
            default -> throw new MybeautipException("Unsupported provider type");
        };

        switch (member.getStatus()) {
            case ACTIVE -> {
                memberRepository.updateLastLoggedAt(member.getId());
                return member;
            }
            case WITHDRAWAL -> throw new MemberNotFoundException("탈퇴한 회원");
            case DORMANT -> {
                ExceptionMemberResponse response = ExceptionMemberResponse.builder()
                        .memberId(member.getId())
                        .date(member.getLastLoggedAt().plusYears(1))
                        .build();
                throw new BadRequestException(ErrorCode.DORMANT_MEMBER, "휴면 회원", response);
            }
            case SUSPENDED -> {
                ExceptionMemberResponse response = ExceptionMemberResponse.builder()
                        .memberId(member.getId())
                        .date(member.getModifiedAtZoned().plusDays(14))
                        .build();
                throw new BadRequestException(ErrorCode.SUSPENDED_MEMBER, "정지 회원", response);
            }
            case EXILE -> throw new NotFoundException(ErrorCode.EXILED_MEMBER, "추방된 회원");
            default -> throw new MybeautipException("Unsupported member status");
        }
    }

    private Member getMemberIdForKakao(String kakaoId) {
        KakaoMember kakaoMember = kakaoMemberRepository.findById(kakaoId)
                .orElseThrow(() -> new MemberNotFoundException("No such kakao member. kakao id - " + kakaoId));

        return memberRepository.findById(kakaoMember.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("No such member. member id - " + kakaoMember.getMemberId()));
    }

    private Member getMemberIdForNaver(String naverId) {
        NaverMember naverMember = naverMemberRepository.findById(naverId)
                .orElseThrow(() -> new MemberNotFoundException("No such naver member. naver id - " + naverId));

        return memberRepository.findById(naverMember.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("No such member. member id - " + naverMember.getMemberId()));
    }

    private Member getMemberIdForFacebook(String facebookId) {
        FacebookMember facebookMember = facebookMemberRepository.findById(facebookId)
                .orElseThrow(() -> new MemberNotFoundException("No such facebook member. facebook id - " + facebookId));

        return memberRepository.findById(facebookMember.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("No such member. member id - " + facebookMember.getMemberId()));
    }

    private Member getMemberIdForApple(String appleId) {
        AppleMember appleMember = appleMemberRepository.findById(appleId)
                .orElseThrow(() -> new MemberNotFoundException("No such facebook member. facebook id - " + appleId));

        return memberRepository.findById(appleMember.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("No such member. member id - " + appleMember.getMemberId()));
    }
}
