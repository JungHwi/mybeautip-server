package com.jocoos.mybeautip.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.client.provider")
public class Oauth2Config {
  private Oauth2ProviderConfig kakao;

  @PostConstruct
  public void postConstruct() {
    log.debug("{}", this);
  }

  @Data
  @NoArgsConstructor
  public static class Oauth2ProviderConfig {
    private String tokenUri;
    private String userInfoUri;
    private String clientId;
    private String clientSecret;
    private String authorizationGrantType;
    private String redirectUri;
  }
}
