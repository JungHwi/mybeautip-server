package com.jocoos.mybeautip.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends MybeautipRuntimeException {

    static final String DEFAULT_MESSAGE = "bad_request";

    public BadRequestException(String description) {
        super(DEFAULT_MESSAGE, description);
    }

    public BadRequestException(String message, String description) {
        super(message, description);
    }

    public BadRequestException(FieldError error) {
        super(DEFAULT_MESSAGE, "");
        this.description = createErrorDescription(error);
    }

    private String createErrorDescription(FieldError error) {
        return String.format("%s %s", error.getField(), error.getDefaultMessage());
    }
}
