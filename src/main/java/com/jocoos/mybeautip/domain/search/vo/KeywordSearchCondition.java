package com.jocoos.mybeautip.domain.search.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.ZonedDateTime;

@AllArgsConstructor
@Getter
public class KeywordSearchCondition {

    private String keyword;

    private ZonedDateTime cursor;

    private int size;

    public String getKeyword() {
        return keyword.trim();
    }
}
