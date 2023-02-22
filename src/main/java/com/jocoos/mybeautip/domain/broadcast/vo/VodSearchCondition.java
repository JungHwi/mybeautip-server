package com.jocoos.mybeautip.domain.broadcast.vo;

import com.jocoos.mybeautip.domain.event.code.SortField;
import com.jocoos.mybeautip.global.vo.CursorPaging;
import com.jocoos.mybeautip.global.vo.SearchOption;
import lombok.Builder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Builder
public record VodSearchCondition(List<Long> categoryIds,
                                 SearchOption searchOption,
                                 Boolean isVisible,
                                 CursorPaging<Long> cursorPaging,
                                 Pageable pageable) {
    public SortField nonUniqueCursor() {
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
