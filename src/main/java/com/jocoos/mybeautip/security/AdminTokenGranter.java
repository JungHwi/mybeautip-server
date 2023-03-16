package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.admin.AdminMemberRepository;
import com.jocoos.mybeautip.global.exception.AuthenticationException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

import java.util.Map;

@Slf4j
public class AdminTokenGranter extends AbstractTokenGranter {

    private final MemberRepository memberRepository;
    private final AdminMemberRepository adminMemberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AdminTokenGranter(
            AuthorizationServerTokenServices tokenServices,
            ClientDetailsService clientDetailsService,
            OAuth2RequestFactory requestFactory,
            MemberRepository memberRepository,
            AdminMemberRepository adminMemberRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider) {
        super(tokenServices, clientDetailsService, requestFactory, "admin");
        this.memberRepository = memberRepository;
        this.adminMemberRepository = adminMemberRepository;
        this.passwordEncoder = passwordEncoder;
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
    protected OAuth2Authentication getOAuth2Authentication(ClientDetails client, TokenRequest tokenRequest) {
        Map<String, String> requestParameters = tokenRequest.getRequestParameters();
        String adminId = requestParameters.get("email");
        String password = requestParameters.get("password");

        log.debug("email: {}, password: {}", adminId, password);

        if (StringUtils.isBlank(adminId)) {
            throw new AuthenticationException("email is required");
        }

        if (adminId.length() > 50) {
            throw new AuthenticationException("The email must be less or equals to 50");
        }

        if (StringUtils.isBlank(password)) {
            throw new AuthenticationException("admin password is required");
        }

        return adminMemberRepository.findById(adminId)
                .filter(m -> passwordEncoder.matches(password, m.getPassword()))
                .map(m -> generateToken(memberRepository.getById(m.getMember().getId()), client, tokenRequest))
                .orElseThrow(() -> new AuthenticationException("email or password Invalid."));
    }

    private OAuth2Authentication generateToken(Member member, ClientDetails client, TokenRequest tokenRequest) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(member.getId(), "");
        return new OAuth2Authentication(tokenRequest.createOAuth2Request(client), authenticationToken);
    }
}
