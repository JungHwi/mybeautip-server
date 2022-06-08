package com.jocoos.mybeautip.exception;

import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;

public class AuthenticationDormantMemberException extends ClientAuthenticationException {
    public AuthenticationDormantMemberException(String msg) {
        super(msg);
    }

    public AuthenticationDormantMemberException(String msg, Throwable t) {
        super(msg, t);
    }

    @Override
    public int getHttpErrorCode() {
        return 404;
    }


    @Override
    public String getOAuth2ErrorCode() {
        return "dormant_member";
    }
}