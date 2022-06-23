package com.jocoos.mybeautip.global.exception;

import org.springframework.http.HttpStatus;

import static com.jocoos.mybeautip.global.constant.ErrorCodeConstant.DEFAULT_ERROR_CODE;

public class NotFoundException extends MybeautipException {

    public NotFoundException() {
        super(HttpStatus.NOT_FOUND, DEFAULT_ERROR_CODE, HttpStatus.NOT_FOUND.getReasonPhrase());
    }

    public NotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, DEFAULT_ERROR_CODE, message);
    }

    public NotFoundException(int code, String message) {
        super(HttpStatus.NOT_FOUND, code, message);
    }
}
