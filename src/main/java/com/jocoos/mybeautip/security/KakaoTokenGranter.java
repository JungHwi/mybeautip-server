package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.domain.event.service.impl.SignupEventService;
import com.jocoos.mybeautip.global.exception.AuthenticationException;
import com.jocoos.mybeautip.global.exception.AuthenticationMemberNotFoundException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.global.util.date.ZonedDateTimeUtil;
import com.jocoos.mybeautip.member.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.Map;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.LOCAL_DATE_FORMAT;

@Slf4j
public class KakaoTokenGranter extends AbstractTokenGranter {

    private final LegacyMemberService legacyMemberService;
    private final MemberRepository memberRepository;
    private final KakaoMemberRepository kakaoMemberRepository;
    private final SignupEventService signupEventService;

    public KakaoTokenGranter(
            AuthorizationServerTokenServices tokenServices,
            ClientDetailsService clientDetailsService,
            OAuth2RequestFactory requestFactory,
            LegacyMemberService legacyMemberService,
            MemberRepository memberRepository,
            KakaoMemberRepository kakaoMemberRepository,
            SignupEventService signupEventService) {
        super(tokenServices, clientDetailsService, requestFactory, "kakao");
        this.legacyMemberService = legacyMemberService;
        this.memberRepository = memberRepository;
        this.kakaoMemberRepository = kakaoMemberRepository;
        this.signupEventService = signupEventService;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> requestParameters = tokenRequest.getRequestParameters();
        String kakaoId = requestParameters.get("social_id");
        String username = requestParameters.get("username");
        log.debug("kakao id: {}, username: {}", kakaoId, username);

        if (StringUtils.isBlank(kakaoId)) {
            throw new AuthenticationException("kakao ID is required");
        }

        if (kakaoId.length() > 30) {
            throw new AuthenticationException("The kakao ID must be less or equals to 30");
        }

        KakaoMember kakaoMember = kakaoMemberRepository.findById(kakaoId)
                .orElseThrow(() -> new AuthenticationMemberNotFoundException("No such kakao member. kakao id - " + kakaoId));

        Member member = memberRepository.findById(kakaoMember.getMemberId())
                .orElseThrow(() -> new AuthenticationMemberNotFoundException("No such member. member id - " + kakaoMember.getMemberId()));

        switch (member.getStatus()) {
            case ACTIVE -> {
                signupEventService.join(member);
                return generateToken(member, client, tokenRequest);
            }
            case DORMANT -> throw new AuthenticationMemberNotFoundException(ErrorCode.DORMANT_MEMBER, "휴면 회원");
            case SUSPENDED -> {
                String date = ZonedDateTimeUtil.toString(member.getModifiedAtZoned().plusDays(14), LOCAL_DATE_FORMAT);
                throw new AuthenticationMemberNotFoundException(ErrorCode.SUSPENDED_MEMBER, date);
            }
            case EXILE -> throw new AuthenticationMemberNotFoundException(ErrorCode.EXILED_MEMBER, "추방된 회원");
            default ->
                    throw new AuthenticationMemberNotFoundException("No such member. member id - " + kakaoMember.getMemberId());
        }
    }

    private OAuth2Authentication generateToken(Member member, ClientDetails client, TokenRequest tokenRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getId(), "");
        return new OAuth2Authentication(tokenRequest.createOAuth2Request(client), authenticationToken);
    }
}
