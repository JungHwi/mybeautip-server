package com.jocoos.mybeautip.domain.notice.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

import static com.jocoos.mybeautip.global.exception.ErrorCode.INVALID_SORT_FIELD;

@Getter
@AllArgsConstructor
public enum NoticeSort implements CodeValue {

    ID("id", "아이디"),
    VIEW_COUNT("viewCount", "조회수");

    private final String column;
    private final String description;

    public static String getColumn(String sort) {
        return Arrays.stream(values())
                .map(NoticeSort::getColumn)
                .filter(value -> value.equals(sort))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(INVALID_SORT_FIELD));
    }

    @Override
    public String getName() {
        return this.column;
    }
}
