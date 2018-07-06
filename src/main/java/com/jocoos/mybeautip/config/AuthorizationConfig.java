package com.jocoos.mybeautip.config;

import com.google.common.collect.Lists;
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
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

@Configuration
@EnableAuthorizationServer
public class AuthorizationConfig extends AuthorizationServerConfigurerAdapter {

  static final String SCOPE_READ = "read";
  static final String SCOPE_WRITE = "write";
  static final int ACCESS_TOKEN_VALIDITY_SECONDS = 1*60*60;
  static final int REFRESH_TOKEN_VALIDITY_SECONDS = 6*60*60;

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


  @Override
  public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    endpoints
        .pathMapping("/oauth/token", "/api/1/token")
        .authenticationManager(authenticationManager)
        .accessTokenConverter(accessTokenConverter())
        .tokenStore(jwtTokenStore())
        .tokenGranter(tokenGranter(endpoints));
  }

  @Override
  public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
    clients.inMemory()
        .withClient("mybeautip-ios")
        .secret(passwordEncoder.encode("akdlqbxlqdkdldhdptm"))
        .authorizedGrantTypes("facebook", "naver", "kakao", "client")
        .scopes(SCOPE_READ, SCOPE_WRITE)
        .accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS)
        .refreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY_SECONDS)
        .and()
        .withClient("mybeautip-android")
        .secret(passwordEncoder.encode("akdlqbxlqdksemfhdlem"))
        .scopes(SCOPE_READ, SCOPE_WRITE)
        .authorizedGrantTypes("facebook", "naver", "kakao", "client")
        .accessTokenValiditySeconds(ACCESS_TOKEN_VALIDITY_SECONDS)
        .refreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY_SECONDS);
  }

  @Bean
  public JwtAccessTokenConverter accessTokenConverter() {
    JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
    jwtAccessTokenConverter.setSigningKey(privateKey);
    jwtAccessTokenConverter.setVerifierKey(publicKey);
    return jwtAccessTokenConverter;
  }

  @Bean
  public JwtTokenStore jwtTokenStore() {
    return new JwtTokenStore(accessTokenConverter());
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
