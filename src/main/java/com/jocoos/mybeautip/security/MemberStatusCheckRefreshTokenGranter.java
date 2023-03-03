package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.domain.member.dto.ExceptionMemberResponse;
import com.jocoos.mybeautip.global.exception.AuthenticationException;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import com.jocoos.mybeautip.global.util.date.ZonedDateTimeUtil;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import static com.jocoos.mybeautip.global.exception.ErrorCode.EXILED_MEMBER;
import static com.jocoos.mybeautip.global.exception.ErrorCode.SUSPENDED_MEMBER;
import static java.lang.Long.parseLong;
import static org.springframework.util.StringUtils.hasText;

public class MemberStatusCheckRefreshTokenGranter extends RefreshTokenGranter {

    private final MemberRepository memberRepository;
    private final JwtTokenProvider tokenProvider;

    public MemberStatusCheckRefreshTokenGranter(AuthorizationServerTokenServices tokenServices,
                                                ClientDetailsService clientDetailsService,
                                                OAuth2RequestFactory requestFactory,
                                                MemberRepository memberRepository,
                                                JwtTokenProvider tokenProvider) {
        super(tokenServices, clientDetailsService, requestFactory);
        this.memberRepository = memberRepository;
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected OAuth2AccessToken getAccessToken(ClientDetails client, TokenRequest tokenRequest) {

        // 기초적인 에러를 잡아주기 때문에 먼저 실행합니다
        OAuth2AccessToken response = super.getAccessToken(client, tokenRequest);

        String refreshToken = tokenRequest.getRequestParameters().get("refresh_token");
        String requestId = tokenProvider.getMemberId(refreshToken);
        checkMemberRefreshToken(requestId);

        return response;
    }

    private void checkMemberRefreshToken(String requestId) {
        if (hasText(requestId) && !requestId.startsWith("guest")) {
            long memberId = parseLong(requestId);
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new MemberNotFoundException("Not found member info. id - " + memberId));
            checkMemberStatus(member);
        }
    }

    private void checkMemberStatus(Member member) {
        switch (member.getStatus()) {
            case SUSPENDED -> {
                ExceptionMemberResponse errorResponse = ExceptionMemberResponse.builder()
                        .memberId(member.getId())
                        .date(ZonedDateTimeUtil.toString(member.getModifiedAtZoned().plusDays(14)))
                        .build();
                throw new AuthenticationException(SUSPENDED_MEMBER, errorResponse);
            }
            case EXILE -> throw new AuthenticationException(EXILED_MEMBER);
        }
    }
}
