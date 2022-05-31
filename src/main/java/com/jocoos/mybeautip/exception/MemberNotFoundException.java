package com.jocoos.mybeautip.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MemberNotFoundException extends MybeautipRuntimeException {

    public MemberNotFoundException() {
        super("member_not_found", "member not found");
    }

    public MemberNotFoundException(String description) {
        super("member_not_found", description);
    }

    public MemberNotFoundException(Long requestId) {
        super("member_not_found", "member not found - " + requestId);
    }

    public MemberNotFoundException(String message, String description) {
        super(message, description);
    }
}
