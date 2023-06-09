package com.jocoos.mybeautip.domain.broadcast.code;

import com.jocoos.mybeautip.global.code.CodeValue;
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
public enum BroadcastSortField implements CodeValue {
    SORTED_STATUS("상태 순서", SORTED_STATUS_ASC, SORTED_STATUS_DESC),
    CREATED_AT("생성 일자", Sort.by(ASC, ID_NAME), Sort.by(DESC, ID_NAME)),
    STARTED_AT("시작 일자", STARTED_AT_ASC, STARTED_AT_DESC),
    ;

    private final String description;
    private final Sort asc;
    private final Sort desc;

    public static Sort getSortBy(BroadcastStatus status) {
        if (status == null) {
            return SORTED_STATUS.getSort(ASC);
        }
        return switch (status) {
            case LIVE -> STARTED_AT.getSort(DESC);
            default -> STARTED_AT.getSort(ASC);
        };
    }

    public Sort getSort(Direction direction) {
        if (direction.isAscending()) return asc;
        else return desc;
    }

    @Override
    public String getName() {
        return name();
    }

    @NoArgsConstructor(access = PRIVATE)
    static class BroadcastSortFieldConstant {
        static final String ID_NAME = "id";
        static final String SORTED_STATUS_NAME = "sortedStatus";
        static final String STARTED_AT_NAME = "startedAt";
        static final Sort STARTED_AT_ASC = Sort.by(ASC, STARTED_AT_NAME, ID_NAME);
        static final Sort STARTED_AT_DESC = Sort.by(DESC, STARTED_AT_NAME, ID_NAME);
        static final Sort SORTED_STATUS_ASC = Sort.by(ASC, SORTED_STATUS_NAME).and(STARTED_AT_DESC);
        static final Sort SORTED_STATUS_DESC = Sort.by(DESC, SORTED_STATUS_NAME).and(STARTED_AT_DESC);
    }
}

