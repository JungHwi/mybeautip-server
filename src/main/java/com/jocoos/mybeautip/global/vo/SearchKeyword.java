package com.jocoos.mybeautip.global.vo;

import lombok.Getter;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.util.date.ZonedDateTimeUtil.toUTCZoned;

@Getter
public class SearchKeyword {
    private final String searchField;
    private final String keyword;
    private final ZonedDateTime startAt;
    private final ZonedDateTime endAt;

    public SearchKeyword(String searchField, String keyword, LocalDate startAt, LocalDate endAt, ZoneId zoneId) {
        this.searchField = searchField;
        this.keyword = keyword;
        this.startAt = startAt == null ? null : toUTCZoned(startAt, zoneId);
        this.endAt = endAt == null ? null : toUTCZoned(endAt, zoneId);
    }

    public static SearchKeyword from(String queryString, LocalDate startAt, LocalDate endAt, ZoneId zoneId) {

        if (!StringUtils.hasText(queryString)) {
            return new SearchKeyword(null, null, startAt, endAt, zoneId);
        }

        String[] splitQueryString = queryString.split(",");
        return new SearchKeyword(splitQueryString[0], splitQueryString[1], startAt, endAt, zoneId);
    }
}
