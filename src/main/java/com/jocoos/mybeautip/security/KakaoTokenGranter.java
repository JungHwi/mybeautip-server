package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.exception.AuthenticationException;
import com.jocoos.mybeautip.exception.AuthenticationMemberNotFoundException;
import com.jocoos.mybeautip.member.KakaoMemberRepository;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.Map;

@Slf4j
public class KakaoTokenGranter extends AbstractTokenGranter {

    private final LegacyMemberService legacyMemberService;
    private final MemberRepository memberRepository;
    private final KakaoMemberRepository kakaoMemberRepository;

    public KakaoTokenGranter(
            AuthorizationServerTokenServices tokenServices,
            ClientDetailsService clientDetailsService,
            OAuth2RequestFactory requestFactory,
            LegacyMemberService legacyMemberService,
            MemberRepository memberRepository,
            KakaoMemberRepository kakaoMemberRepository) {
        super(tokenServices, clientDetailsService, requestFactory, "kakao");
        this.legacyMemberService = legacyMemberService;
        this.memberRepository = memberRepository;
        this.kakaoMemberRepository = kakaoMemberRepository;
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

        return kakaoMemberRepository.findById(kakaoId)
                .map(m -> generateToken(memberRepository.getById(m.getMemberId()), client, tokenRequest))
                .orElseThrow(() -> new AuthenticationMemberNotFoundException("No such kakao member. kakao social id - " + kakaoId));
    }

    private OAuth2Authentication generateToken(Member member, ClientDetails client, TokenRequest tokenRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getId(), "");
        return new OAuth2Authentication(tokenRequest.createOAuth2Request(client), authenticationToken);
    }
}
