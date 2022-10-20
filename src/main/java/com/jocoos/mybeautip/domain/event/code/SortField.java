package com.jocoos.mybeautip.domain.event.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SortField {
    CREATED_AT("createdAt"),
    JOIN_COUNT("joinCount");

    private final String fieldName;

    public static SortField from(String sortFieldString) {
        return Arrays.stream(SortField.values())
                .filter(sortField -> sortField.fieldName.equalsIgnoreCase(sortFieldString))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
