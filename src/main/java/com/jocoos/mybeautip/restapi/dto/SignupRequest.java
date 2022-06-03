package com.jocoos.mybeautip.restapi.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class SignupRequest {
    @NotEmpty()
    private String socialId;
    private String grantType;
    private String username;
    private String email;
    private String avatarUrl;
}