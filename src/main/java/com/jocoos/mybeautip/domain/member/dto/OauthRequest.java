package com.jocoos.mybeautip.domain.member.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class OauthRequest {
    @NotNull
    String code;
    String state = "mybeautip-web-mobile";
}
