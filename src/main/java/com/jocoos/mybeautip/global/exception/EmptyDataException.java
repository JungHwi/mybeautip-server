package com.jocoos.mybeautip.global.exception;

import static com.jocoos.mybeautip.global.constant.ErrorCodeConstant.DEFAULT_ERROR_CODE;

public class EmptyDataException extends BadRequestException {
    public EmptyDataException() {
        super(DEFAULT_ERROR_CODE, "Empty Data");
    }

    public EmptyDataException(String message) {
        super(DEFAULT_ERROR_CODE, message);
    }

    public EmptyDataException(int code, String message) {
        super(code, message);
    }
}