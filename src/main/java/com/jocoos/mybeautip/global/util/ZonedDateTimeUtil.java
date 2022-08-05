package com.jocoos.mybeautip.global.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

public class ZonedDateTimeUtil {

    public static String toString(ZonedDateTime zonedDateTime) {
        return toString(zonedDateTime, ZONE_DATE_TIME_FORMAT);
    }

    public static String toString(ZonedDateTime zonedDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return zonedDateTime.format(formatter);
    }
}
