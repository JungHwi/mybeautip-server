package com.jocoos.mybeautip.global.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class NotificationException extends MybeautipException {

    public NotificationException(String message) {
        super(message);
    }
}
