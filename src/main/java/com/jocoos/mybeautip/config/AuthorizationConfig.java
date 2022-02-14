package com.jocoos.mybeautip.config;

import javax.annotation.PostConstruct;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.admin.AdminMemberRepository;
import com.jocoos.mybeautip.member.*;
import com.jocoos.mybeautip.security.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.refresh.RefreshTokenGranter;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

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
        .accessTokenConverter(jwtTokenEnhencer())
        .tokenGranter(tokenGranter(endpoints))
        .tokenStore(jwtTokenStore())
        .userDetailsService(userDetailService);
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
  public JwtAccessTokenConverter jwtTokenEnhencer() {
    JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
    jwtAccessTokenConverter.setSigningKey(privateKey);
    jwtAccessTokenConverter.setVerifierKey(publicKey);
    jwtAccessTokenConverter.setAccessTokenConverter(accessTokenConverter());
    return jwtAccessTokenConverter;
  }

  @Bean
  public JwtTokenStore jwtTokenStore() {
    return new JwtTokenStore(jwtTokenEnhencer());
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
        Lists.newArrayList(
            new FacebookTokenGranter(
                endpoints.getTokenServices(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(),
                memberRepository,
                facebookMemberRepository),
            new NaverTokenGranter(
                endpoints.getTokenServices(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(),
                memberRepository,
                naverMemberRepository),
            new KakaoTokenGranter(
                endpoints.getTokenServices(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(),
                memberRepository,
                kakaoMemberRepository),
            new AppleTokenGranter(
                endpoints.getTokenServices(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory(),
                memberRepository,
                appleMemberRepository),
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
                passwordEncoder),
             new RefreshTokenGranter(endpoints.getTokenServices(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory())
        )
    );
  }
}
