package com.jocoos.mybeautip.domain.broadcast.vo;

import com.jocoos.mybeautip.domain.event.code.SortField;
import com.jocoos.mybeautip.global.vo.CursorPaging;
import lombok.Builder;
import org.springframework.data.domain.Sort;

import java.util.List;

@Builder
public record VodSearchCondition(List<Long> categoryIds,
                                 CursorPaging<Long> cursorPaging) {
    public SortField sortField() {
        return cursorPaging.nonUniqueCursor();
    }

    public Long cursor() {
        return cursorPaging.uniqueCursor();
    }

    public Sort getSort() {
        return cursorPaging.pageable().getSort();
    }

    public long getPageSize() {
        return cursorPaging.pageable().getPageSize();
    }
}
