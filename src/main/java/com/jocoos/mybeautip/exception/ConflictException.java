package com.jocoos.mybeautip.exception;

public class ConflictException extends MybeautipRuntimeException {

    public ConflictException(String description) {
        super("bad_request", description);
    }

    public ConflictException(String message, String description) {
        super(message, description);
    }
}
