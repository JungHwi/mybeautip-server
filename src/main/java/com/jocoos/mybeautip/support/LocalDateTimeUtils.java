package com.jocoos.mybeautip.support;

import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

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

    public static Date getStartDateByMonth() {
        return getStartDateByMonth(YearMonth.now());
    }

    public static Date getEndDateByMonth() {
        return getEndDateByMonth(YearMonth.now());
    }

    public static Date getStartDateByMonth(YearMonth yearMonth) {
        return toDate(yearMonth.atDay(1).atStartOfDay());
    }

    public static Date getEndDateByMonth(YearMonth yearMonth) {
        return toDate(yearMonth.atEndOfMonth().atTime(LocalTime.MAX));
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}
