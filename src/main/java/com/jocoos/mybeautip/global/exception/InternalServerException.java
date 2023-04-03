package com.jocoos.mybeautip.global.exception;

import lombok.Getter;

@Getter
public class InternalServerException extends RuntimeException {

    private final ErrorCode errorCode;

    public InternalServerException(String description) {
        super(description);
        this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    }


    public InternalServerException(ErrorCode errorCode, String description) {
        super(description);
        this.errorCode = errorCode;
    }

    public InternalServerException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getDescription(), cause);
        this.errorCode = errorCode;
    }
}
