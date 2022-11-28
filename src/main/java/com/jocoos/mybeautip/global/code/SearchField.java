package com.jocoos.mybeautip.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SearchField {
    TITLE("title"),
    COMMENT("comment");

    private final String fieldName;

    public static SearchField get(String searchField) {
        return Arrays.stream(values())
                .filter(value -> value.fieldName.equals(searchField))
                .findFirst()
                .orElse(null);
    }
}
