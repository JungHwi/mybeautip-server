package com.jocoos.mybeautip.global.vo;

import com.jocoos.mybeautip.global.code.SortField;

public record CursorPaging<T>(T uniqueCursor,
                              SortField nonUniqueCursor) {

    public static CursorPaging<Long> idCursorWithNonUniqueSortField(Long id,
                                                                    SortField sortField) {
        return new CursorPaging<>(id, sortField);
    }
}
