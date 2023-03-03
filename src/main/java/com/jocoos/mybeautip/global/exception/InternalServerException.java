package com.jocoos.mybeautip.global.exception;

import lombok.Getter;

@Getter
public class InternalServerException extends RuntimeException {

    private final ErrorCode errorCode;

    public InternalServerException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getDescription(), cause);
        this.errorCode = errorCode;
    }
}
