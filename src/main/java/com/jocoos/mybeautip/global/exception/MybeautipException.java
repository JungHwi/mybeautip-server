package com.jocoos.mybeautip.global.exception;

import lombok.Getter;

@Getter
public class MybeautipException extends RuntimeException {
    protected String description;

    public MybeautipException(String message) {
        super(message);
    }

    public MybeautipException(String message, String description) {
        super(message);
        this.description = description;
    }

    public MybeautipException(String message, Throwable e) {
        super(message, e);
    }
}
