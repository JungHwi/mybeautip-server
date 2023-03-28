package com.jocoos.mybeautip.global.code;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;

import java.util.Arrays;

import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Getter
@RequiredArgsConstructor
public enum SortField implements CodeValue {

    ID("아이디", "id"),
    CREATED_AT("생성일시", "createdAt"),
    JOIN_COUNT("참여수", "joinCount"),
    VIEW_COUNT("조회순", "viewCount"),
    TOTAL_HEART_COUNT("총 하트순", "totalHeartCount"),
    LIKE_COUNT("좋이요순", "likeCount"),
    REPORT_COUNT("신고순", "reportCount");

    private final String description;
    private final String fieldName;

    public static SortField from(String sortFieldString) {
        return Arrays.stream(SortField.values())
                .filter(sortField -> sortField.fieldName.equalsIgnoreCase(sortFieldString))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }

    public static Sort withIdDesc(SortField field) {
        return withId(field.getFieldName(), DESC);
    }

    public static Sort withIdAsc(SortField field) {
        return withId(field.getFieldName(), ASC);
    }

    private static Sort withId(String property, Sort.Direction direction) {
        return Sort.by(direction, property, ID.getFieldName());
    }

    @Override
    public String getName() {
        return this.name();
    }
}
