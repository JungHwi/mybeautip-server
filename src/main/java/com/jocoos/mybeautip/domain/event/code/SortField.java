package com.jocoos.mybeautip.domain.event.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SortField {
    CREATED_AT("createdAt"),
    JOIN_COUNT("joinCount");

    private final String fieldName;
}
