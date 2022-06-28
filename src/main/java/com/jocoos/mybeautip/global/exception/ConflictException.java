package com.jocoos.mybeautip.global.exception;

public class ConflictException extends MybeautipException {

    public ConflictException(String description) {
        super("bad_request", description);
    }

    public ConflictException(String message, String description) {
        super(message, description);
    }
}
