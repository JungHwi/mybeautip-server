package com.jocoos.mybeautip.domain.event.vo;

import com.jocoos.mybeautip.domain.event.code.SortField;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static com.jocoos.mybeautip.domain.event.code.SortField.JOIN_COUNT;

@Getter
@RequiredArgsConstructor
public class Sort {

    private final SortField sortField;
    private final String direction;

    public boolean isOrderByJoinCount() {
        return JOIN_COUNT.equals(sortField);
    }
}
