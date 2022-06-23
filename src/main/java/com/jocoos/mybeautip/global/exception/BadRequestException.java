package com.jocoos.mybeautip.global.exception;

import org.springframework.http.HttpStatus;

import static com.jocoos.mybeautip.global.constant.ErrorCodeConstant.DEFAULT_ERROR_CODE;

public class BadRequestException extends MybeautipException {
    public BadRequestException() {
        super(HttpStatus.BAD_REQUEST, DEFAULT_ERROR_CODE, HttpStatus.BAD_REQUEST.getReasonPhrase());
    }

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, DEFAULT_ERROR_CODE, message);
    }

    public BadRequestException(int code, String message) {
        super(HttpStatus.BAD_REQUEST, code, message);
    }
}