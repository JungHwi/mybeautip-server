package com.jocoos.mybeautip.client.apple.dto;

import lombok.Setter;

@Setter
public class AppleTokenResponse {
    private String access_token;
    private String expires_in;
    private String id_token;
    private String refresh_token;
    private String token_type;
    private String error;

    public String getAccessToken() {
        return access_token;
    }

    public String getExpiresIn() {
        return expires_in;
    }

    public String getIdToken() {
        return id_token;
    }

    public String getRefreshToken() {
        return refresh_token;
    }

    public String getTokenType() {
        return token_type;
    }
}
