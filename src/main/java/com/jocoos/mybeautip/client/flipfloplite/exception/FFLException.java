package com.jocoos.mybeautip.client.flipfloplite.exception;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLErrorCode;
import lombok.Getter;

@Getter
public class FFLException extends RuntimeException {
    private final FFLErrorCode errorCode;
    private final String errorMessage;

    public FFLException(String errorCode, String errorMessage) {
        super(errorCode);
        this.errorCode = FFLErrorCode.of(errorCode);
        String message = errorMessage;
        if (!errorCode.equals(this.errorCode.name())) {
            message = String.format("[%s] %s", errorCode, errorMessage);
        }
        this.errorMessage = message;
    }
}
