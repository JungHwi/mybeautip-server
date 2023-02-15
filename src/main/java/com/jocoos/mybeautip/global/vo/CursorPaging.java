package com.jocoos.mybeautip.global.vo;

import com.jocoos.mybeautip.domain.event.code.SortField;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;

public record CursorPaging<T>(T uniqueCursor, SortField nonUniqueCursor, Pageable pageable) {
    public static CursorPaging<Long> idCursorWithNonUniqueSortField(Long id,
                                                                    SortField sortField,
                                                                    String order,
                                                                    int size) {
        Pageable pageable = PageRequest.of(
                0,
                size,
                Direction.fromString(order),
                sortField.getFieldName(), "id"
        );
        return new CursorPaging<>(id, sortField, pageable);
    }
}
