package com.jocoos.mybeautip.global.exception;

import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;

public class AuthenticationException extends ClientAuthenticationException {

    public AuthenticationException(String msg) {
        super(msg);
    }

    public AuthenticationException(String msg, Throwable t) {
        super(msg, t);
    }

    @Override
    public int getHttpErrorCode() {
        return 401;
    }

    @Override
    public String getOAuth2ErrorCode() {
        return "mybeautip authentication error";
    }
}
