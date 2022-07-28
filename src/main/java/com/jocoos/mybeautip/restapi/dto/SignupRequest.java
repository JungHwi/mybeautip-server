package com.jocoos.mybeautip.restapi.dto;

import com.jocoos.mybeautip.domain.term.code.TermType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import java.util.Set;

@Getter
@Setter
public class SignupRequest {
    @NotEmpty()
    private String socialId;
    private String grantType;
    private String username;
    private String email;
    private String avatarUrl;
    private String refreshToken;
    private Set<TermType> termTypes;
}
