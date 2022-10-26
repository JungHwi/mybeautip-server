package com.jocoos.mybeautip.domain.event.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum SortField implements CodeValue {
    CREATED_AT("생성일시", "createdAt"),
    JOIN_COUNT("참여수", "joinCount");

    private final String description;
    private final String fieldName;

    public static SortField from(String sortFieldString) {
        return Arrays.stream(SortField.values())
                .filter(sortField -> sortField.fieldName.equalsIgnoreCase(sortFieldString))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public String getName() {
        return this.name();
    }
}
