package com.jocoos.mybeautip.client.flipfloplite.exception;

import com.jocoos.mybeautip.client.flipfloplite.code.FFLErrorCode;
import lombok.Getter;

@Getter
public class FFLException extends RuntimeException {
    private final FFLErrorCode errorCode;
    private final String errorMessage;

    public FFLException(FFLErrorCode errorCode, String errorMessage) {
        super(errorCode.name());
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
