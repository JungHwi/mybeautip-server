package com.jocoos.mybeautip.restapi.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String grantType;
    private String socialId;
    private String username;
    private String email;
    private String avatarUrl;
}