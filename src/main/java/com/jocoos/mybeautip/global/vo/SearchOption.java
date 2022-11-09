package com.jocoos.mybeautip.global.vo;

import com.jocoos.mybeautip.support.DateUtils;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static com.jocoos.mybeautip.global.util.date.ZonedDateTimeUtil.toUTCZoned;

@Getter
public class SearchOption {
    private final String searchField;
    private final String keyword;
    private final ZonedDateTime startAt;
    private final ZonedDateTime endAt;

    private SearchOption(String searchField, String keyword, LocalDate startAt, LocalDate endAt, ZoneId zoneId) {
        this.searchField = searchField;
        this.keyword = keyword;
        this.startAt = startAt == null ? null : toUTCZoned(startAt, zoneId);
        this.endAt = endAt == null ? null : toUTCZoned(endAt, zoneId);
    }

    public static SearchOption from(String queryString, LocalDate startAt, LocalDate endAt, ZoneId zoneId) {

        if (!StringUtils.hasText(queryString)) {
            return new SearchOption(null, null, startAt, endAt, zoneId);
        }

        String[] splitQueryString = queryString.split(",");
        return new SearchOption(splitQueryString[0], splitQueryString[1], startAt, endAt, zoneId);
    }

    public Date getStartAtDate() {
        return DateUtils.toDate(startAt);
    }

    public Date getEndAtDate() {
        return DateUtils.toDate(endAt);
    }

    public boolean isNoSearch() {
        return searchField == null || keyword == null;
    }
}
