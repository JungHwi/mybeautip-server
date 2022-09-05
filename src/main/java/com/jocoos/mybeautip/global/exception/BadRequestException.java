package com.jocoos.mybeautip.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends MybeautipException {
    static final String DEFAULT_MESSAGE = "bad_request";

    public BadRequestException(String description) {
        super(ErrorCode.BAD_REQUEST, description);
    }

    public BadRequestException(String message, String description) {
        super(message, description);
    }

    public BadRequestException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }

    public BadRequestException(FieldError error) {
        super(ErrorCode.BAD_REQUEST, "");
        this.description = createErrorDescription(error);
    }

    public BadRequestException(String message, Throwable e) {
        super(message, e);
    }

    private String createErrorDescription(FieldError error) {
        return String.format("%s %s", error.getField(), error.getDefaultMessage());
    }
}
