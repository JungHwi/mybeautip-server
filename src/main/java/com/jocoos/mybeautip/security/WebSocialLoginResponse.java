package com.jocoos.mybeautip.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WebSocialLoginResponse {

    private boolean result;
    private SocialMemberRequest socialMember;
    private AccessTokenResponse token;

    WebSocialLoginResponse(AccessTokenResponse token) {
        this.result = true;
        this.token = token;
    }

    WebSocialLoginResponse(SocialMemberRequest socialMemberRequest) {
        this.result = false;
        this.socialMember = socialMemberRequest;
    }
}
