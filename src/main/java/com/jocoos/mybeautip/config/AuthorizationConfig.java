package com.jocoos.mybeautip.config;

import com.jocoos.mybeautip.admin.AdminMemberRepository;
import com.jocoos.mybeautip.domain.event.service.impl.SignupEventService;
import com.jocoos.mybeautip.global.exception.AuthenticationException;
import com.jocoos.mybeautip.global.exception.AuthenticationMemberNotFoundException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.global.exception.ErrorResponse;
import com.jocoos.mybeautip.member.*;
import com.jocoos.mybeautip.security.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.error.WebResponseExceptionTranslator;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Slf4j
@Configuration
@EnableAuthorizationServer
public class AuthorizationConfig extends AuthorizationServerConfigurerAdapter {

    static final String SCOPE_READ = "read";
    static final String SCOPE_WRITE = "write";
    static final String SCOPE_ADMIN = "admin";

    static final String GRANT_TYPE_FACEBOOK = "facebook";
    static final String GRANT_TYPE_NAVER = "naver";
    static final String GRANT_TYPE_KAKAO = "kakao";
    static final String GRANT_TYPE_APPLE = "apple";
    static final String GRANT_TYPE_CLIENT = "client";
    static final String GRANT_TYPE_ADMIN = "admin";
    static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    @Autowired
    private LegacyMemberService legacyMemberService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FacebookMemberRepository facebookMemberRepository;

    @Autowired
    private NaverMemberRepository naverMemberRepository;

    @Autowired
    private KakaoMemberRepository kakaoMemberRepository;

    @Autowired
    private AppleMemberRepository appleMemberRepository;

    @Autowired
    private AdminMemberRepository adminMemberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${security.oauth2.private-key}")
    private String privateKey;

    @Value("${security.oauth2.public-key}")
    private String publicKey;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private MybeautipUserDetailsService userDetailService;

    @Autowired
    private SignupEventService signupEventService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Value("${mybeautip.security.access-token-validity-seconds}")
    private int accessTokenValiditySeconds;

    @Value("${mybeautip.security.refresh-token-validity-seconds}")
    private int refreshTokenValiditySeconds;

    @PostConstruct
    public void postConstruct() {
        log.debug("accessTokenValiditySeconds: {}, refreshTokenValiditySeconds: {}", accessTokenValiditySeconds, refreshTokenValiditySeconds);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) {
        endpoints
                .pathMapping("/oauth/token", "/api/1/token")
                .authenticationManager(authenticationManager)
                .accessTokenConverter(jwtTokenEnhancer())
                .tokenGranter(tokenGranter(endpoints))
                .tokenStore(jwtTokenStore())
                .userDetailsService(userDetailService)
                .exceptionTranslator(authorizationWebResponseExceptionTranslator());
    }

    public WebResponseExceptionTranslator authorizationWebResponseExceptionTranslator() {
        return (Exception e) -> {

            if (e instanceof AuthenticationMemberNotFoundException ex) {
                return toErrorResponse(ex.getErrorCode(), ex.getMessage(), ex.getContents(), ex.getHttpErrorCode());
            }

            if (e instanceof AuthenticationException ex) {
                return toErrorResponse(ex.getErrorCode(), ex.getMessage(), ex.getContents(), ex.getHttpErrorCode());
            }

            return ResponseEntity.status(UNAUTHORIZED).body(Map.of("message", e.getMessage()));
        };
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                .withClient("mybeautip-ios")
                .secret(passwordEncoder.encode("akdlqbxlqdkdldhdptm"))
                .authorizedGrantTypes(GRANT_TYPE_FACEBOOK, GRANT_TYPE_NAVER, GRANT_TYPE_KAKAO, GRANT_TYPE_APPLE, GRANT_TYPE_CLIENT, GRANT_TYPE_REFRESH_TOKEN)
                .scopes(SCOPE_READ, SCOPE_WRITE)
                .accessTokenValiditySeconds(accessTokenValiditySeconds)
                .refreshTokenValiditySeconds(refreshTokenValiditySeconds)
                .and()
                .withClient("mybeautip-android")
                .secret(passwordEncoder.encode("akdlqbxlqdksemfhdlem"))
                .scopes(SCOPE_READ, SCOPE_WRITE)
                .authorizedGrantTypes(GRANT_TYPE_FACEBOOK, GRANT_TYPE_NAVER, GRANT_TYPE_KAKAO, GRANT_TYPE_CLIENT, GRANT_TYPE_REFRESH_TOKEN)
                .accessTokenValiditySeconds(accessTokenValiditySeconds)
                .refreshTokenValiditySeconds(refreshTokenValiditySeconds)
                .and()
                .withClient("mybeautip-mobile")
                .secret(passwordEncoder.encode("akdlqbxlqahqkdlf"))
                .scopes(SCOPE_READ, SCOPE_WRITE, SCOPE_ADMIN)
                .authorizedGrantTypes(GRANT_TYPE_FACEBOOK, GRANT_TYPE_NAVER, GRANT_TYPE_KAKAO, GRANT_TYPE_CLIENT, GRANT_TYPE_REFRESH_TOKEN)
                .accessTokenValiditySeconds(accessTokenValiditySeconds)
                .refreshTokenValiditySeconds(refreshTokenValiditySeconds)
                .and()
                .withClient("mybeautip-web")
                .secret(passwordEncoder.encode("akdlqbxlqdjemals"))
                .scopes(SCOPE_READ, SCOPE_WRITE, SCOPE_ADMIN)
                .authorizedGrantTypes(GRANT_TYPE_ADMIN, GRANT_TYPE_REFRESH_TOKEN)
                .accessTokenValiditySeconds(accessTokenValiditySeconds)
                .refreshTokenValiditySeconds(refreshTokenValiditySeconds);
    }

    @Bean
    public JwtAccessTokenConverter jwtTokenEnhancer() {
        JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
        jwtAccessTokenConverter.setSigningKey(privateKey);
        jwtAccessTokenConverter.setVerifierKey(publicKey);
        jwtAccessTokenConverter.setAccessTokenConverter(accessTokenConverter());
        return jwtAccessTokenConverter;
    }

    @Bean
    public JwtTokenStore jwtTokenStore() {
        return new JwtTokenStore(jwtTokenEnhancer());
    }

    private AccessTokenConverter accessTokenConverter() {
        DefaultUserAuthenticationConverter userAuthenticationConverter = new DefaultUserAuthenticationConverter();
        userAuthenticationConverter.setUserDetailsService(userDetailService);

        DefaultAccessTokenConverter accessTokenConverter = new DefaultAccessTokenConverter();
        accessTokenConverter.setUserTokenConverter(userAuthenticationConverter);
        return accessTokenConverter;
    }

    private CompositeTokenGranter tokenGranter(AuthorizationServerEndpointsConfigurer endpoints) {

        return new CompositeTokenGranter(
                Arrays.asList(
                        new FacebookTokenGranter(
                                endpoints.getTokenServices(),
                                endpoints.getClientDetailsService(),
                                endpoints.getOAuth2RequestFactory(),
                                legacyMemberService,
                                memberRepository,
                                facebookMemberRepository,
                                signupEventService,
                                jwtTokenProvider),
                        new NaverTokenGranter(
                                endpoints.getTokenServices(),
                                endpoints.getClientDetailsService(),
                                endpoints.getOAuth2RequestFactory(),
                                legacyMemberService,
                                memberRepository,
                                naverMemberRepository,
                                signupEventService,
                                jwtTokenProvider),
                        new KakaoTokenGranter(
                                endpoints.getTokenServices(),
                                endpoints.getClientDetailsService(),
                                endpoints.getOAuth2RequestFactory(),
                                legacyMemberService,
                                memberRepository,
                                kakaoMemberRepository,
                                signupEventService,
                                jwtTokenProvider),
                        new AppleTokenGranter(
                                endpoints.getTokenServices(),
                                endpoints.getClientDetailsService(),
                                endpoints.getOAuth2RequestFactory(),
                                legacyMemberService,
                                memberRepository,
                                appleMemberRepository,
                                signupEventService,
                                jwtTokenProvider),
                        new ClientTokenGranter(
                                endpoints.getTokenServices(),
                                endpoints.getClientDetailsService(),
                                endpoints.getOAuth2RequestFactory()),
                        new AdminTokenGranter(
                                endpoints.getTokenServices(),
                                endpoints.getClientDetailsService(),
                                endpoints.getOAuth2RequestFactory(),
                                memberRepository,
                                adminMemberRepository,
                                passwordEncoder,
                                jwtTokenProvider),
                        new MemberStatusCheckRefreshTokenGranter(endpoints.getTokenServices(),
                                endpoints.getClientDetailsService(),
                                endpoints.getOAuth2RequestFactory(),
                                memberRepository,
                                jwtTokenProvider)
                )
        );
    }

    private ResponseEntity<ErrorResponse> toErrorResponse(ErrorCode errorCode, String description, Object contents, int statusCode) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(errorCode.name().toLowerCase())
                .errorDescription(description)
                .contents(contents)
                .build();
        return ResponseEntity.status(statusCode).body(errorResponse);
    }
}
