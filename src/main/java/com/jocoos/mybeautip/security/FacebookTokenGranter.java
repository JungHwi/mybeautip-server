package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.domain.event.service.impl.SignupEventService;
import com.jocoos.mybeautip.global.exception.AuthenticationDormantMemberException;
import com.jocoos.mybeautip.global.exception.AuthenticationException;
import com.jocoos.mybeautip.global.exception.AuthenticationMemberNotFoundException;
import com.jocoos.mybeautip.member.*;
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
    private final SignupEventService signupEventService;

    public FacebookTokenGranter(
            AuthorizationServerTokenServices tokenServices,
            ClientDetailsService clientDetailsService,
            OAuth2RequestFactory requestFactory,
            LegacyMemberService legacyMemberService,
            MemberRepository memberRepository,
            FacebookMemberRepository facebookMemberRepository,
            SignupEventService signupEventService) {
        super(tokenServices, clientDetailsService, requestFactory, "facebook");
        this.legacyMemberService = legacyMemberService;
        this.memberRepository = memberRepository;
        this.facebookMemberRepository = facebookMemberRepository;
        this.signupEventService = signupEventService;

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

        FacebookMember facebookMember = facebookMemberRepository.findById(facebookId)
                .orElseThrow(() -> new AuthenticationMemberNotFoundException("No such facebook member. facebook id - " + facebookId));

        Member member = memberRepository.findById(facebookMember.getMemberId())
                .orElseThrow(() -> new AuthenticationMemberNotFoundException("No such member. member id - " + facebookMember.getMemberId()));

        switch (member.getStatus()) {
            case ACTIVE:
                signupEventService.join(member);
                return generateToken(member, client, tokenRequest);
            case DORMANT:
                throw new AuthenticationDormantMemberException("Dormant Member. member id - " + facebookMember.getMemberId());
            default:
                throw new AuthenticationMemberNotFoundException("No such member. member id - " + facebookMember.getMemberId());
        }
    }

    private OAuth2Authentication generateToken(Member member, ClientDetails client, TokenRequest tokenRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getId(), "");
        return new OAuth2Authentication(tokenRequest.createOAuth2Request(client), authenticationToken);
    }
}
