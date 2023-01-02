package com.jocoos.mybeautip.domain.notice.code;

import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NoticeSort implements CodeValue {

    ID("id", "아이디"),
    VIEW_COUNT("viewCount", "조회수");

    private final String column;
    private final String description;

    @Override
    public String getName() {
        return this.name();
    }
}
