package com.jocoos.mybeautip.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SearchField {
    TITLE("title"),
    COMMENT("comment");

    private final String filedName;

    public static SearchField get(String searchField) {
        return Arrays.stream(values())
                .filter(value -> value.filedName.equals(searchField))
                .findFirst()
                .orElse(null);
    }
}
