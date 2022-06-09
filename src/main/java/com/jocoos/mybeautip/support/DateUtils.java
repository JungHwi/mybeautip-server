package com.jocoos.mybeautip.support;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class DateUtils {
    public static ZoneId ZONE_SEOUL = ZoneId.of("Asia/Seoul");
    public static ZoneId UTC = ZoneId.of("UTC");

    public static Date toDate(String longValue) {
        return toDate(longValue, ZoneId.of("GMT+9"));
    }

    public static Date toDate(String longValue, ZoneId zoneId) {
        LocalDateTime ldt =
                LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(longValue)), ZoneId.systemDefault());
        ZoneId zone = zoneId == null ? ZoneId.systemDefault() : zoneId;
        return Date.from(ldt.atZone(zone).toInstant());
    }

    public static LocalDate toLocalDate(Date date) {
        return toLocalDate(date, ZONE_SEOUL);
    }

    private static LocalDate toLocalDate(Date date, ZoneId zoneId) {
        if (zoneId == null) {
            return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        }
        return date.toInstant().atZone(zoneId).toLocalDate();
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return toLocalDateTime(date, ZONE_SEOUL);
    }

    public static LocalDateTime toLocalDateTime(Date date, ZoneId zoneId) {
        return date.toInstant().atZone(zoneId).toLocalDateTime();
    }

    public static Date toDate(LocalDate dateToConvert) {
        return Date.from(dateToConvert.atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date stringFormatToDate(String date) {
        return fromString(date, "yyyyMMdd HHmmss", ZONE_SEOUL);
    }

    private static Date fromString(String date, String format, ZoneId zoneId) {
        LocalDateTime localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ofPattern(format));
        return Date.from(localDateTime.atZone(zoneId).toInstant());
    }

    public static Date addYear(int year) {
        LocalDateTime localDateTime = LocalDateTime.now().plusYears(year);
        return toDate(localDateTime);
    }

    public static String toFormat(Date date) {
        return toFormat(date, "yyyy-MM-dd HH:mm:ss");
    }

    private static String toFormat(Date date, String format) {
        return toLocalDateTime(date).format(DateTimeFormatter.ofPattern(format));
    }
}
