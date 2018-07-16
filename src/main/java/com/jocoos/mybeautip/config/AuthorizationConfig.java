package com.jocoos.mybeautip.config;

import javax.annotation.PostConstruct;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.member.FacebookMemberRepository;
import com.jocoos.mybeautip.member.KakaoMemberRepository;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.NaverMemberRepository;
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

  static final String GRANT_TYPE_FACEBOOK = "facebook";
  static final String GRANT_TYPE_NAVER = "naver";
  static final String GRANT_TYPE_KAKAO = "kakao";
  static final String GRANT_TYPE_CLIENT = "client";

  @Autowired
  private MemberRepository memberRepository;

  @Autowired
  private FacebookMemberRepository facebookMemberRepository;

  @Autowired
  private NaverMemberRepository naverMemberRepository;

  @Autowired
  private KakaoMemberRepository kakaoMemberRepository;

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
    log.debug("accessTokenValiditySeconds: {}", accessTokenValiditySeconds);
    log.debug("refreshTokenValiditySeconds: {}", refreshTokenValiditySeconds);
  }

  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
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
        .authorizedGrantTypes(GRANT_TYPE_FACEBOOK, GRANT_TYPE_NAVER, GRANT_TYPE_KAKAO, GRANT_TYPE_CLIENT)
        .scopes(SCOPE_READ, SCOPE_WRITE)
        .accessTokenValiditySeconds(accessTokenValiditySeconds)
        .refreshTokenValiditySeconds(refreshTokenValiditySeconds)
        .and()
        .withClient("mybeautip-android")
        .secret(passwordEncoder.encode("akdlqbxlqdksemfhdlem"))
        .scopes(SCOPE_READ, SCOPE_WRITE)
        .authorizedGrantTypes(GRANT_TYPE_FACEBOOK, GRANT_TYPE_NAVER, GRANT_TYPE_KAKAO, GRANT_TYPE_CLIENT)
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
            new ClientTokenGranter(
                endpoints.getTokenServices(),
                endpoints.getClientDetailsService(),
                endpoints.getOAuth2RequestFactory())
        )
    );
  }
}
