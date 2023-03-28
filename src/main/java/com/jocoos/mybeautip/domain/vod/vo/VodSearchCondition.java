package com.jocoos.mybeautip.domain.vod.vo;

import com.jocoos.mybeautip.global.code.SortField;
import com.jocoos.mybeautip.domain.vod.code.VodStatus;
import com.jocoos.mybeautip.global.vo.CursorPaging;
import com.jocoos.mybeautip.global.vo.SearchOption;
import lombok.Builder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.ZonedDateTime;
import java.util.List;

@Builder
public record VodSearchCondition(List<Long> categoryIds,
                                 SearchOption searchOption,
                                 Boolean isVisible,
                                 VodStatus status,
                                 CursorPaging<Long> cursorPaging,
                                 Pageable pageable) {

    public VodSearchCondition {
        if (searchOption == null) {
            searchOption = SearchOption.NO_OPTION;
        }
    }

    public SortField nonUniqueCursor() {
        return cursorPaging.nonUniqueCursor();
    }

    public Long cursor() {
        return cursorPaging.uniqueCursor();
    }

    public Sort getSort() {
        return pageable.getSort();
    }

    public long offset() {
        return pageable.getOffset();
    }

    public long pageSize() {
        return pageable.getPageSize();
    }

    public String keyword() {
        return searchOption.getKeyword();
    }

    public ZonedDateTime startAt() {
        return searchOption.getStartAt();
    }

    public ZonedDateTime endAt() {
        return searchOption.getEndAt();
    }

    public Boolean isReported() {
        return searchOption.getIsReported();
    }
}
