package com.jocoos.mybeautip.domain.vod.code;

import com.jocoos.mybeautip.global.code.SortField;
import com.jocoos.mybeautip.global.code.CodeValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

@Getter
@RequiredArgsConstructor
public enum VodSortField implements CodeValue {

    CREATED_AT("생성일자", SortField.CREATED_AT, SortField.withIdAsc(SortField.CREATED_AT), SortField.withIdDesc(SortField.CREATED_AT)),
    VIEW_COUNT("조회수", SortField.VIEW_COUNT, SortField.withIdAsc(SortField.VIEW_COUNT), SortField.withIdDesc(SortField.VIEW_COUNT)),
    HEART_COUNT("하트수", SortField.TOTAL_HEART_COUNT, SortField.withIdAsc(SortField.TOTAL_HEART_COUNT), SortField.withIdDesc(SortField.TOTAL_HEART_COUNT));

    private final String description;
    private final SortField sortField;
    private final Sort asc;
    private final Sort desc;

    public Sort getSort(Direction direction) {
        if (direction.isAscending()) return asc;
        else return desc;
    }

    @Override
    public String getName() {
        return name();
    }
}
