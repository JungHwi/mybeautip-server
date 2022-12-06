package com.jocoos.mybeautip.global.exception;

import lombok.Getter;

@Getter
public class MybeautipException extends RuntimeException {
    protected ErrorCode errorCode;
    protected String description;
    protected Object response;

    public MybeautipException(String description) {
        super(ErrorCode.INTERNAL_SERVER_ERROR.getKey());
        this.errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
        this.description = description;
        this.response = null;
    }

    public MybeautipException(ErrorCode errorCode, String description) {
        super(errorCode.getKey());
        this.errorCode = errorCode;
        this.description = description;
        this.response = null;
    }

    public MybeautipException(ErrorCode errorCode, String description, Object response) {
        super(errorCode.getKey());
        this.errorCode = errorCode;
        this.description = description;
        this.response = response;
    }

    public MybeautipException(ErrorCode errorCode, Throwable e) {
        super(errorCode.getKey(), e);
        this.response = null;
    }
}
