package com.jocoos.mybeautip.global.exception;

import static com.jocoos.mybeautip.global.exception.ErrorCode.FILE_TOO_BIG;

public class FileSizeExceedException extends BadRequestException {

    public FileSizeExceedException(long limitByte, long requestByte) {
        super(FILE_TOO_BIG, String.format("limit is %d byte, request size is %d byte", limitByte, requestByte));
    }
}
