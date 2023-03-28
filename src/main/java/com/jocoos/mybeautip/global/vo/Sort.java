package com.jocoos.mybeautip.global.vo;

import com.jocoos.mybeautip.global.code.SortField;
import lombok.Getter;

import static com.jocoos.mybeautip.global.code.SortField.JOIN_COUNT;

@Getter
public class Sort {

    private final SortField sortField;
    private final String direction;

    public Sort(String sortField, String direction) {
        this.sortField = SortField.from(sortField);
        this.direction = direction;
    }

    public boolean isOrderByJoinCount() {
        return JOIN_COUNT.equals(sortField);
    }
}
