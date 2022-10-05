package com.jocoos.mybeautip.domain.search.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_MILLI_FORMAT;

@AllArgsConstructor
@Getter
public class KeywordSearchCondition {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(ZONE_DATE_TIME_MILLI_FORMAT);
    private String keyword;

    private String cursor;

    private int size;

    public String getKeyword() {
        return keyword.trim();
    }

    public ZonedDateTime cursorZonedDateTime() {
        return StringUtils.isBlank(cursor) ? null : ZonedDateTime.parse(cursor, dateTimeFormatter);
    }

    public Date cursorDate() {
        return StringUtils.isBlank(cursor) ? new Date() : new Date(Long.parseLong(cursor));
    }
}
