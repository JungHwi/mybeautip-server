package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.domain.event.service.impl.SignupEventService;
import com.jocoos.mybeautip.domain.member.dto.ExceptionMemberResponse;
import com.jocoos.mybeautip.global.exception.AuthenticationException;
import com.jocoos.mybeautip.global.exception.AuthenticationMemberNotFoundException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.global.util.StringConvertUtil;
import com.jocoos.mybeautip.global.util.date.ZonedDateTimeUtil;
import com.jocoos.mybeautip.member.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
public class AppleTokenGranter extends AbstractTokenGranter {

    private final LegacyMemberService legacyMemberService;
    private final MemberRepository memberRepository;
    private final AppleMemberRepository appleMemberRepository;
    private final SignupEventService signupEventService;
    private final JwtTokenProvider jwtTokenProvider;

    public AppleTokenGranter(
            AuthorizationServerTokenServices tokenServices,
            ClientDetailsService clientDetailsService,
            OAuth2RequestFactory requestFactory,
            LegacyMemberService legacyMemberService,
            MemberRepository memberRepository,
            AppleMemberRepository appleMemberRepository,
            SignupEventService signupEventService,
            JwtTokenProvider jwtTokenProvider) {
        super(tokenServices, clientDetailsService, requestFactory, "apple");
        this.legacyMemberService = legacyMemberService;
        this.memberRepository = memberRepository;
        this.appleMemberRepository = appleMemberRepository;
        this.signupEventService = signupEventService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public OAuth2AccessToken getAccessToken(ClientDetails client, TokenRequest tokenRequest) {
        OAuth2AccessToken token = super.getTokenServices().createAccessToken(getOAuth2Authentication(client, tokenRequest));
        String refreshToken = token.getRefreshToken().getValue();
        String username = jwtTokenProvider.getMemberId(refreshToken);
        jwtTokenProvider.registerRefreshToken(username, refreshToken);
        return token;
    }

    @Override
    @Transactional
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> requestParameters = tokenRequest.getRequestParameters();
        String appleId = requestParameters.get("social_id");
        String name = requestParameters.get("name");
        String email = requestParameters.get("email");
        log.debug("apple id: {}, email: {}, name: {}", appleId, name, email);

        if (StringUtils.isBlank(appleId)) {
            throw new AuthenticationException("Apple ID is required");
        }

        AppleMember appleMember = appleMemberRepository.findById(appleId)
                .orElseThrow(() -> new AuthenticationMemberNotFoundException("No such apple member. apple id - " + appleId));

        Member member = memberRepository.findById(appleMember.getMemberId())
                .orElseThrow(() -> new AuthenticationMemberNotFoundException("No such member. member id - " + appleMember.getMemberId()));

        switch (member.getStatus()) {
            case ACTIVE -> {
                signupEventService.join(member);
                memberRepository.updateLastLoggedAt(member.getId());
                return generateToken(member, client, tokenRequest);
            }
            case DORMANT -> {
                ExceptionMemberResponse response = ExceptionMemberResponse.builder()
                        .memberId(member.getId())
                        .date(ZonedDateTimeUtil.toString(member.getModifiedAtZoned()))
                        .build();
                String responseString = StringConvertUtil.convertToJson(response);
                throw new AuthenticationMemberNotFoundException(ErrorCode.DORMANT_MEMBER, responseString);
            }
            case SUSPENDED -> {
                ExceptionMemberResponse response = ExceptionMemberResponse.builder()
                        .memberId(member.getId())
                        .date(ZonedDateTimeUtil.toString(member.getModifiedAtZoned().plusDays(14)))
                        .build();
                throw new AuthenticationMemberNotFoundException(ErrorCode.SUSPENDED_MEMBER, "정지 회원", response);
            }
            case EXILE -> throw new AuthenticationMemberNotFoundException(ErrorCode.EXILED_MEMBER, "추방된 회원");
            default ->
                    throw new AuthenticationMemberNotFoundException("No such member. member id - " + appleMember.getMemberId());
        }
    }

    private OAuth2Authentication generateToken(Member member, ClientDetails client, TokenRequest tokenRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getId(), "");
        return new OAuth2Authentication(tokenRequest.createOAuth2Request(client), authenticationToken);
    }
}
