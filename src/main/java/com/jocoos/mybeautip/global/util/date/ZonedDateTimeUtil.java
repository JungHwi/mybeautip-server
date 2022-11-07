package com.jocoos.mybeautip.global.util.date;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

public class ZonedDateTimeUtil {

    public static final ZoneId UTC = ZoneId.of("UTC");

    public static boolean isZonedDateTime(String dateTime) {
        return isZonedDateTime(dateTime, ZONE_DATE_TIME_FORMAT);
    }

    public static boolean isZonedDateTime(String dateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        DateValidatorUsingFormatter validator = new DateValidatorUsingFormatter(formatter);
        return validator.isValid(dateTime);
    }

    public static String toString(ZonedDateTime zonedDateTime) {
        return toString(zonedDateTime, ZONE_DATE_TIME_FORMAT);
    }

    public static String toString(ZonedDateTime zonedDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return zonedDateTime.format(formatter);
    }

    public static ZonedDateTime toZonedDateTime(String dateTime) {
        return toZonedDateTime(dateTime, ZONE_DATE_TIME_FORMAT);
    }

    public static ZonedDateTime toUTCZoned(String dateTime) {
        return LocalDateTime.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S")).atZone(UTC);
    }

    public static ZonedDateTime toZonedDateTime(String dateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return ZonedDateTime.parse(dateTime, formatter);
    }

    public static ZonedDateTime toUTCZoned(Date cursor) {
        if (cursor == null) {
            return null;
        }
        return ZonedDateTime.ofInstant(cursor.toInstant(), UTC);
    }

    public static ZonedDateTime toUTCZoned(LocalDate localDate, ZoneId zoneId) {
        if (localDate == null) {
            return null;
        }
        return localDate.atStartOfDay(zoneId).withZoneSameInstant(UTC);
    }
}
