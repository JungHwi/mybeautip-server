package com.jocoos.mybeautip.global.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;

@Getter
@SuperBuilder
@AllArgsConstructor
public class SearchRequest {
    protected final String searchField;
    protected final String keyword;
    protected final ZonedDateTime startAt;
    protected final ZonedDateTime endAt;
    protected final Pageable pageable;

    public SearchRequest(Pageable pageable) {
        this.searchField = null;
        this.keyword = null;
        this.startAt = null;
        this.endAt = null;
        this.pageable = pageable;
    }
}
