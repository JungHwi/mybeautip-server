package com.jocoos.mybeautip.exception;

import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;

public class AuthenticationMemberNotFoundException extends ClientAuthenticationException {
    public AuthenticationMemberNotFoundException(String msg) {
        super(msg);
    }

    public AuthenticationMemberNotFoundException(String msg, Throwable t) {
        super(msg, t);
    }

    @Override
    public int getHttpErrorCode() {
        return 404;
    }


    @Override
    public String getOAuth2ErrorCode() {
        return "member_not_found";
    }
}