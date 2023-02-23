package com.jocoos.mybeautip.client.flipfloplite.config;

import com.jocoos.mybeautip.global.config.feign.FeignClientConfig;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

public class FlipFlopLiteClientConfig extends FeignClientConfig {

    @Bean
    public RequestInterceptor requestInterceptor() throws InterruptedException {
        return requestTemplate -> requestTemplate.header(AUTHORIZATION, FlipFlopLiteProperties.getBasicToken());
    }
}
