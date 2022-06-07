package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.exception.AuthenticationException;
import com.jocoos.mybeautip.exception.AuthenticationMemberNotFoundException;
import com.jocoos.mybeautip.member.FacebookMemberRepository;
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
public class FacebookTokenGranter extends AbstractTokenGranter {

    private final LegacyMemberService legacyMemberService;
    private final MemberRepository memberRepository;
    private final FacebookMemberRepository facebookMemberRepository;

    public FacebookTokenGranter(
            AuthorizationServerTokenServices tokenServices,
            ClientDetailsService clientDetailsService,
            OAuth2RequestFactory requestFactory,
            LegacyMemberService legacyMemberService,
            MemberRepository memberRepository,
            FacebookMemberRepository facebookMemberRepository) {
        super(tokenServices, clientDetailsService, requestFactory, "facebook");
        this.legacyMemberService = legacyMemberService;
        this.memberRepository = memberRepository;
        this.facebookMemberRepository = facebookMemberRepository;
    }

    @Override
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> requestParameters = tokenRequest.getRequestParameters();
        String facebookId = requestParameters.get("social_id");
        String username = requestParameters.get("social_id");
        log.debug("facebook id: {}, username: {}", facebookId, username);

        if (StringUtils.isBlank(facebookId)) {
            throw new AuthenticationException("facebook ID is required");
        }

        if (facebookId.length() > 20) {
            throw new AuthenticationException("The facebook ID must be less or equals to 20");
        }

        return facebookMemberRepository.findById(facebookId)
                .map(m -> generateToken(memberRepository.getById(m.getMemberId()), client, tokenRequest))
                .orElseThrow(() -> new AuthenticationMemberNotFoundException("No such facebook member. facebook social id - " + facebookId));
    }

    private OAuth2Authentication generateToken(Member member, ClientDetails client, TokenRequest tokenRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getId(), "");
        return new OAuth2Authentication(tokenRequest.createOAuth2Request(client), authenticationToken);
    }
}
