package com.jocoos.mybeautip.global.exception;

public class NotFoundException extends MybeautipException {

    public NotFoundException(String message, String description) {
        super(message, description);
    }

    public NotFoundException(String message) {
        super(message);
    }
}
