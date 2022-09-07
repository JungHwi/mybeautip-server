package com.jocoos.mybeautip.global.exception;

public class NotFoundException extends MybeautipException {

    public NotFoundException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }

    public NotFoundException(String description) {
        super(ErrorCode.NOT_FOUND, description);
    }
}
