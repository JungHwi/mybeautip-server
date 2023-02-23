package com.jocoos.mybeautip.client.flipfloplite.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class FlipFlopLiteProperties {

    private final String apiKey;
    private final String apiSecret;

    @Getter
    private static String basicToken;

    public FlipFlopLiteProperties(@Value("${ffl.api-key}") String apiKey,
                                  @Value("${ffl.api-secret}") String apiSecret) {
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
        basicToken = "Basic " + Base64.getEncoder().encodeToString(String.format("%s:%s", apiKey, apiSecret).getBytes());
    }
}
