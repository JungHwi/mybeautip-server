package com.jocoos.mybeautip.global.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import static com.jocoos.mybeautip.global.constant.ErrorCodeConstant.DEFAULT_ERROR_CODE;

@Getter
@Setter
public class MybeautipException extends RuntimeException {
    private HttpStatus httpStatus;
    private int code;

    public MybeautipException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public MybeautipException(String message) {
        super(message);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        this.code = DEFAULT_ERROR_CODE;
    }

    public MybeautipException(HttpStatus httpStatus, int code, String message) {
        super(message);
        this.httpStatus = httpStatus;
        this.code = code;
    }
}