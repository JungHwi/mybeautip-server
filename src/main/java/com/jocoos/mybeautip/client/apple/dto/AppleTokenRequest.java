package com.jocoos.mybeautip.client.apple.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class AppleTokenRequest {

    private String code;
    private String client_id;
    private String client_secret;
    private String grant_type;
    private String refresh_token;

}

