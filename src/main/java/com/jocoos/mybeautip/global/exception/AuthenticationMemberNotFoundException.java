package com.jocoos.mybeautip.global.exception;

import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;

public class AuthenticationMemberNotFoundException extends ClientAuthenticationException {
    private ErrorCode errorCode;

    public AuthenticationMemberNotFoundException(String msg) {
        super(msg);
        this.errorCode = ErrorCode.MEMBER_NOT_FOUND;
    }

    public AuthenticationMemberNotFoundException(ErrorCode errorCode, String msg) {
        super(msg);
        this.errorCode =  errorCode;
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
        return this.errorCode.name();
    }


}