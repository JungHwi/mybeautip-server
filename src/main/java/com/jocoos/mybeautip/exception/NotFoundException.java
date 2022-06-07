package com.jocoos.mybeautip.exception;

public class NotFoundException extends MybeautipRuntimeException {

    public NotFoundException(String message, String description) {
        super(message, description);
    }

    public NotFoundException(String message) {
        super(message);
    }
}
