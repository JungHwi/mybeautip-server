package com.jocoos.mybeautip.global.exception;

import com.jocoos.mybeautip.global.util.date.ZonedDateTimeUtil;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.LOCAL_DATE_FORMAT;

public class NotFoundException extends MybeautipException {

    public NotFoundException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }

    public NotFoundException(String description) {
        super(ErrorCode.NOT_FOUND, description);
    }

    public NotFoundException(ErrorCode errorCode, ZonedDateTime zonedDateTime, String description) {
        super(errorCode, description, ZonedDateTimeUtil.toString(zonedDateTime, LOCAL_DATE_FORMAT));
    }
}
