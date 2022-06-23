package com.jocoos.mybeautip.global.exception;

import org.springframework.http.HttpStatus;

import static com.jocoos.mybeautip.global.constant.ErrorCodeConstant.DEFAULT_ERROR_CODE;

public class ForbiddenException extends MybeautipException {
    public ForbiddenException() {
        super(HttpStatus.FORBIDDEN, DEFAULT_ERROR_CODE, "Forbidden.");
    }

    public ForbiddenException(String message) {
        super(HttpStatus.FORBIDDEN, DEFAULT_ERROR_CODE, message);
    }
}
