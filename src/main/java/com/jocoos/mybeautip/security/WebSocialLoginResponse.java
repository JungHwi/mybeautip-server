package com.jocoos.mybeautip.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebSocialLoginResponse {

    private boolean result;
    private SocialMember socialMember;
    private AccessTokenResponse token;

    WebSocialLoginResponse(AccessTokenResponse token) {
        this.result = true;
        this.token = token;
    }

    WebSocialLoginResponse(SocialMember socialMember) {
        this.result = false;
        this.socialMember = socialMember;
    }
}
