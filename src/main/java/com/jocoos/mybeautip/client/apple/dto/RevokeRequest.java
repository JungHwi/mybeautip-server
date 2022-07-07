package com.jocoos.mybeautip.client.apple.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class RevokeRequest {
    private String client_id;
    private String client_secret;
    private String token;
    private String token_type_hint;
}
