package com.jocoos.mybeautip.domain.search.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Date;


@Getter
@RequiredArgsConstructor
public class KeywordSearchCondition {

    private final String keyword;
    private final ZonedDateTime cursor;

    private final int size;

    public String getKeyword() {
        return keyword.trim();
    }

    public Date cursorDate() {
        return cursor == null ? null : Date.from(cursor.toInstant());
    }
}
