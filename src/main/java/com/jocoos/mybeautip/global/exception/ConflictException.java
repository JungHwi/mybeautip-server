package com.jocoos.mybeautip.global.exception;

public class ConflictException extends MybeautipException {

    public ConflictException(String description) {
        super(ErrorCode.BAD_REQUEST, description);
    }

    public ConflictException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}
