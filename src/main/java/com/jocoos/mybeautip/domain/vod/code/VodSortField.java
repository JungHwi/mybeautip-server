package com.jocoos.mybeautip.domain.vod.code;

import com.jocoos.mybeautip.domain.event.code.SortField;
import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum VodSortField implements CodeValue {

    CREATED_AT("생성일자", SortField.CREATED_AT),
    VIEW_COUNT("조회수", SortField.VIEW_COUNT),
    HEART_COUNT("하트수", SortField.TOTAL_HEART_COUNT)
    ;

    private final String description;
    private final SortField sortField;

    @Override
    public String getName() {
        return name();
    }
}
