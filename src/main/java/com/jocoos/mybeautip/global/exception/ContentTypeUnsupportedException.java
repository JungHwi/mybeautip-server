package com.jocoos.mybeautip.global.exception;

import static com.jocoos.mybeautip.global.exception.ErrorCode.FILE_CONTENT_TYPE_UNSUPPORTED;

public class ContentTypeUnsupportedException extends BadRequestException {

    public ContentTypeUnsupportedException(String requestFileType) {
        super(FILE_CONTENT_TYPE_UNSUPPORTED, "unsupported file content type " + requestFileType);
    }
}
