package com.jocoos.mybeautip.domain.broadcast.code;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import static com.jocoos.mybeautip.domain.broadcast.code.BroadcastSortField.BroadcastSortFieldConstant.*;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.data.domain.Sort.Direction.ASC;
import static org.springframework.data.domain.Sort.Direction.DESC;

@Getter
@RequiredArgsConstructor
public enum BroadcastSortField {
    SORTED_STATUS("상태 순서", SORTED_STATUS_FIELD_NAME, SORTED_STATUS_ASC, SORTED_STATUS_DESC),
    CREATED_AT("생성 일자", ID_FIELD_NAME, Sort.by(ASC, ID_FIELD_NAME), Sort.by(DESC, ID_FIELD_NAME)),
    STARTED_AT("시작 일자", STARTED_AT_FIELD_NAME, STARTED_AT_ASC, STARTED_AT_DESC),
    ;

    private final String description;
    private final String fieldName;
    private final Sort asc;
    private final Sort desc;

    public Sort getSort(Direction direction) {
        if (direction.isAscending()) return asc;
        else return desc;
    }

    @NoArgsConstructor(access = PRIVATE)
    static class BroadcastSortFieldConstant {
        static final String ID_FIELD_NAME = "id";
        static final String SORTED_STATUS_FIELD_NAME = "sortedStatus";
        static final String STARTED_AT_FIELD_NAME = "startedAt";
        private static final Sort STARTED_AT_ID_DESC = Sort.by(DESC, STARTED_AT_FIELD_NAME, ID_FIELD_NAME);
        static final Sort SORTED_STATUS_ASC = Sort.by(ASC, SORTED_STATUS_FIELD_NAME).and(STARTED_AT_ID_DESC);
        static final Sort SORTED_STATUS_DESC = Sort.by(DESC, SORTED_STATUS_FIELD_NAME).and(STARTED_AT_ID_DESC);
        static final Sort STARTED_AT_ASC = Sort.by(ASC, STARTED_AT_FIELD_NAME, ID_FIELD_NAME);
        static final Sort STARTED_AT_DESC = Sort.by(DESC, STARTED_AT_FIELD_NAME, ID_FIELD_NAME);
    }
}

