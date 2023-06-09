package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Slf4j
public class Dates {
    private static final SimpleDateFormat RECOMMENDED_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmssZ");

    public static Date parse(String date) {
        return getRecommendedDate(date);
    }

    public static Date getRecommendedDate(String date) {
        try {
            return RECOMMENDED_DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            log.error("invalid recommended date format", e);
            throw new BadRequestException(ErrorCode.INVALID_DATE_FORMAT, e.getMessage() + " - " + date);
        }
    }

    public static String toString(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String toString(Date date, ZoneId zoneId) {
        return LocalDateTime.ofInstant(date.toInstant(), zoneId).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static Date afterMonths(Date date, int months) {
        LocalDateTime localDateTime = getLocalDateTime(date).plusMonths(months);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date afterMonthsFromNow(int months) {
        LocalDateTime localDateTime = LocalDateTime.now().plusMonths(months);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime getLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}
