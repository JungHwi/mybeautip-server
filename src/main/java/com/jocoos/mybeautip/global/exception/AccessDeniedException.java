package com.jocoos.mybeautip.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class AccessDeniedException extends MybeautipException {

    public AccessDeniedException(String description) {
        super(ErrorCode.ACCESS_DENIED, description);
    }

    public AccessDeniedException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}
