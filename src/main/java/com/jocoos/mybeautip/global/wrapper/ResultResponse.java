package com.jocoos.mybeautip.global.wrapper;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class ResultResponse<T> extends ResponseEntity<T> {
    private int code;
    private String message;
    private T result;

    public ResultResponse() {
        super(HttpStatus.OK);
        this.code = HttpStatus.OK.value();
    }

    public ResultResponse(T result) {
        super(HttpStatus.OK);
        this.code = HttpStatus.OK.value();
        this.message = "success";
        this.result = result;
    }

    public ResultResponse(int code, String message) {
        super(HttpStatus.OK);
        this.code = code;
        this.message = message;
    }

    public ResultResponse(HttpStatus httpStatus) {
        super(httpStatus);
        this.code = httpStatus.value();
        this.message = httpStatus.name();
    }

    public ResultResponse(HttpStatus httpStatus, int code, String message) {
        super(httpStatus);
        this.code = code;
        this.message = message;
    }
}
