package com.jocoos.mybeautip.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class MemberNotFoundException extends MybeautipException {

    public MemberNotFoundException() {
        super(ErrorCode.MEMBER_NOT_FOUND, "member not found");
    }

    public MemberNotFoundException(String description) {
        super(ErrorCode.MEMBER_NOT_FOUND, description);
    }

    public MemberNotFoundException(Long requestId) {
        super(ErrorCode.MEMBER_NOT_FOUND, "member not found - " + requestId);
    }

    public MemberNotFoundException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}
