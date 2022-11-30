package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.domain.event.service.impl.SignupEventService;
import com.jocoos.mybeautip.domain.member.dto.ExceptionMemberResponse;
import com.jocoos.mybeautip.global.exception.AuthenticationException;
import com.jocoos.mybeautip.global.exception.AuthenticationMemberNotFoundException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.global.util.StringConvertUtil;
import com.jocoos.mybeautip.member.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Slf4j
public class NaverTokenGranter extends AbstractTokenGranter {

    private final LegacyMemberService legacyMemberService;
    private final MemberRepository memberRepository;
    private final NaverMemberRepository naverMemberRepository;
    private final SignupEventService signupEventService;

    public NaverTokenGranter(
            AuthorizationServerTokenServices tokenServices,
            ClientDetailsService clientDetailsService,
            OAuth2RequestFactory requestFactory,
            LegacyMemberService legacyMemberService,
            MemberRepository memberRepository,
            NaverMemberRepository naverMemberRepository,
            SignupEventService signupEventService) {

        super(tokenServices, clientDetailsService, requestFactory, "naver");
        this.legacyMemberService = legacyMemberService;
        this.memberRepository = memberRepository;
        this.naverMemberRepository = naverMemberRepository;
        this.signupEventService = signupEventService;
    }

    @Override
    @Transactional
    public OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> requestParameters = tokenRequest.getRequestParameters();
        String naverId = requestParameters.get("social_id");
        String username = requestParameters.get("social_id");

        log.debug("naver id: {}, username: {}", naverId, username);

        if (StringUtils.isBlank(naverId)) {
            throw new AuthenticationException("naver ID is required");
        }

        if (naverId.length() > 30) {
            throw new AuthenticationException("The naver ID must be less or equals to 30");
        }

        NaverMember naverMember = naverMemberRepository.findById(naverId)
                .orElseThrow(() -> new AuthenticationMemberNotFoundException("No such naver member. naver id - " + naverId));

        Member member = memberRepository.findById(naverMember.getMemberId())
                .orElseThrow(() -> new AuthenticationMemberNotFoundException("No such member. member id - " + naverMember.getMemberId()));

        switch (member.getStatus()) {
            case ACTIVE -> {
                signupEventService.join(member);
                memberRepository.updateLastLoggedAt(member.getId());
                return generateToken(member, client, tokenRequest);
            }
            case DORMANT -> {
                ExceptionMemberResponse response = ExceptionMemberResponse.builder()
                        .memberId(member.getId())
                        .date(member.getLastLoggedAt().plusYears(1))
                        .build();
                String responseString = StringConvertUtil.convertToJson(response);
                throw new AuthenticationMemberNotFoundException(ErrorCode.DORMANT_MEMBER, responseString);
            }
            case SUSPENDED -> {
                ExceptionMemberResponse response = ExceptionMemberResponse.builder()
                        .memberId(member.getId())
                        .date(member.getModifiedAtZoned().plusDays(14))
                        .build();
                String responseString = StringConvertUtil.convertToJson(response);
                throw new AuthenticationMemberNotFoundException(ErrorCode.SUSPENDED_MEMBER, responseString);
            }
            case EXILE -> throw new AuthenticationMemberNotFoundException(ErrorCode.EXILED_MEMBER, "추방된 회원");
            default ->
                    throw new AuthenticationMemberNotFoundException("No such member. member id - " + naverMember.getMemberId());
        }
    }

    private OAuth2Authentication generateToken(Member member, ClientDetails client, TokenRequest tokenRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getId(), "");
        return new OAuth2Authentication(tokenRequest.createOAuth2Request(client), authenticationToken);
    }
}
