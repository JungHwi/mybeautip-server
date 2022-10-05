package com.jocoos.mybeautip.domain.search.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_MILLI_FORMAT;


@Getter
@RequiredArgsConstructor
public class KeywordSearchCondition {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(ZONE_DATE_TIME_MILLI_FORMAT);
    private final String keyword;

    private final String cursor;

    private final int size;

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
