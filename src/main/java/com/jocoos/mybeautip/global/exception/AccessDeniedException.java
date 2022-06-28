package com.jocoos.mybeautip.global.exception;

public class AccessDeniedException extends MybeautipException {

    public AccessDeniedException(String description) {
        super("access denied", description);
    }
}
