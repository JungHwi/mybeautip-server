package com.jocoos.mybeautip.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.client.provider")
public class Oauth2Config {

    private Oauth2ProviderConfig kakao;
    private Oauth2ProviderConfig naver;
    private Oauth2ProviderConfig facebook;
    private Oauth2ProviderConfig apple;

    @Data
    @NoArgsConstructor
    public static class Oauth2ProviderConfig {
        private String tokenUri;
        private String userInfoUri;
        private String clientId;
        private String clientSecret;
        private String authorizationGrantType;
        private String tokenMethod;
        private String fields;
        private String redirectUri;
        private String teamId;
        private String appBundleId;
    }
}