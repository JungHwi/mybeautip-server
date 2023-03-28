package com.jocoos.mybeautip.global.vo;

import com.jocoos.mybeautip.global.code.SearchDomain;
import com.jocoos.mybeautip.global.code.SearchField;
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

    public static final SearchOption NO_OPTION = SearchOption.builder().build();

    private final SearchDomain domain;
    private final String searchField;
    private final String keyword;
    private final ZonedDateTime startAt;
    private final ZonedDateTime endAt;
    private final Boolean isReported;
    private final Boolean isTopFix;
    private final Boolean isInfluencer;

    public static SearchOption onlyKeyword(String keyword) {
        return SearchOption.builder()
                .keyword(keyword)
                .build();
    }

    @Builder
    private SearchOption(SearchDomain domain,
                         String searchField,
                         String keyword,
                         String searchQueryString,
                         LocalDate startAt,
                         LocalDate endAt,
                         ZoneId zoneId,
                         Boolean isReported,
                         Boolean isTopFix,
                         Boolean isInfluencer) {
        this.domain = domain;
        this.searchField = searchField == null ? setSearchField(searchQueryString) : searchField;
        this.keyword = keyword == null ? setKeyword(searchQueryString) : keyword;
        this.startAt = startAt == null ? null : toUTCZoned(startAt, zoneId);
        this.endAt = endAt == null ? null : toUTCZoned(endAt, zoneId);
        this.isReported = isReported;
        this.isTopFix = isTopFix;
        this.isInfluencer = isInfluencer;
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

    public boolean isNoKeywordSearch() {
        return searchField == null || keyword == null;
    }

    public boolean isOuterField() {
        return domain.isOuterField(searchField);
    }

    public boolean isSearchFieldEqual(SearchField searchField) {
        return searchField == SearchField.get(this.searchField);
    }
}
