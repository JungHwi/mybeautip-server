package com.jocoos.mybeautip.global.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends MybeautipException {
    public UnauthorizedException() {
        super(HttpStatus.UNAUTHORIZED, 40100, HttpStatus.UNAUTHORIZED.getReasonPhrase());
    }

    public UnauthorizedException(String message) {
        super(HttpStatus.UNAUTHORIZED, 40100, message);
    }

    public UnauthorizedException(int code, String message) {
        super(HttpStatus.UNAUTHORIZED, code, message);
    }
}
