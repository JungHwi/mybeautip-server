package com.jocoos.mybeautip.support;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.LOCAL_DATE_FORMAT;

public final class LocalDateTimeUtils {

    public static String toString(LocalDate localDate) {
        return toString(localDate, LOCAL_DATE_FORMAT);
    }

    public static String toString(LocalDate localDate, String format) {
        if (localDate == null || StringUtils.isBlank(format)) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return localDate.format(formatter);
    }

    public static LocalDate toLocalDate(String localDateString) {
        return toLocalDate(localDateString, LOCAL_DATE_FORMAT);
    }

    public static LocalDate toLocalDate(String localDateString, String format) {
        if (StringUtils.isBlank(localDateString) || StringUtils.isBlank(format)) {
            return null;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDate.parse(localDateString, formatter);
    }
}
