package com.jocoos.mybeautip.global.vo;

import com.jocoos.mybeautip.support.DateUtils;
import lombok.Builder;
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
    private final Boolean isReported;

    @Builder
    private SearchOption(String searchQueryString, LocalDate startAt, LocalDate endAt, ZoneId zoneId, Boolean isReported) {
        this.searchField = setSearchField(searchQueryString);
        this.keyword = setKeyword(searchQueryString);
        this.startAt = startAt == null ? null : toUTCZoned(startAt, zoneId);
        this.endAt = endAt == null ? null : toUTCZoned(endAt, zoneId);
        this.isReported = isReported;
    }

    private String setSearchField(String queryString) {
        return StringUtils.hasText(queryString) ? queryString.split(",")[0] : null;
    }

    private String setKeyword(String queryString) {
        return StringUtils.hasText(queryString) ? queryString.split(",")[1] : null;
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
