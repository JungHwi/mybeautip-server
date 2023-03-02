package com.jocoos.mybeautip.domain.broadcast.dto;

import com.jocoos.mybeautip.global.vo.SearchOption;

import java.time.LocalDate;
import java.time.ZoneId;

public record VodFilter(String startAt,
                        String endAt,
                        String searchField,
                        String searchKeyword,
                        Boolean isVisible,
                        Boolean isReported) {
    public SearchOption searchOption() {
        return SearchOption.builder()
                .searchField(searchField)
                .keyword(searchKeyword)
                .startAt(startAt == null ? null : LocalDate.parse(startAt))
                .endAt(endAt == null ? null : LocalDate.parse(endAt))
                .zoneId(ZoneId.of("Asia/Seoul"))
                .isReported(isReported)
                .build();
    }
}
