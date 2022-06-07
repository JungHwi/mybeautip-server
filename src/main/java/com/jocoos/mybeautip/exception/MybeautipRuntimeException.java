package com.jocoos.mybeautip.exception;

import lombok.Getter;

@Getter
public class MybeautipRuntimeException extends RuntimeException {

    protected String description;

    public MybeautipRuntimeException(String message) {
        super(message);
    }

    public MybeautipRuntimeException(String message, String description) {
        super(message);
        this.description = description;
    }
}
