package com.jocoos.mybeautip.global.wrapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.RESPONSE_CODE_OK;

@Getter
@Builder
@AllArgsConstructor
public class ResultResponse<T> {
    private int code;
    private String message;
    private T result;

    public ResultResponse() {
        this.code = RESPONSE_CODE_OK;
    }

    public ResultResponse(T result) {
        this.code = RESPONSE_CODE_OK;
        this.message = "success";
        this.result = result;
    }

    public ResultResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
