package com.jocoos.mybeautip.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends MybeautipException {
    private Object contents;

    public BadRequestException(String description) {
        super(ErrorCode.BAD_REQUEST, description);
    }

    public BadRequestException(ErrorCode errorCode, String description) {
        super(errorCode, description);
        this.contents = null;
    }

    public BadRequestException(ErrorCode errorCode, String description, Object contents) {
        super(errorCode, description);
        this.contents = contents;
    }

    public BadRequestException(FieldError error) {
        super(ErrorCode.BAD_REQUEST, "");
        this.description = createErrorDescription(error);
    }

    public BadRequestException(ErrorCode errorCode, Throwable e) {
        super(errorCode, e);
    }

    private String createErrorDescription(FieldError error) {
        return String.format("%s %s", error.getField(), error.getDefaultMessage());
    }
}
