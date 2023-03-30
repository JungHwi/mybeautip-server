package com.jocoos.mybeautip.domain.broadcast.vo;

import com.jocoos.mybeautip.domain.broadcast.code.BroadcastStatus;
import com.jocoos.mybeautip.global.vo.Between;
import com.jocoos.mybeautip.global.vo.SearchOption;
import lombok.Builder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.ZonedDateTime;
import java.util.List;

@Builder
public record BroadcastSearchCondition(Long memberId,
                                       List<BroadcastStatus> statuses,
                                       Pageable pageable,
                                       SearchOption searchOption,
                                       Between day,
                                       Long cursor) {
    public ZonedDateTime startAt() {
        return searchOption == null ? null : searchOption.getStartAt();
    }

    public ZonedDateTime endAt() {
        return searchOption == null ? null : searchOption.getEndAt();
    }

    public int size() {
        return pageable().getPageSize();
    }

    public long offset() {
        return pageable().getOffset();
    }

    public ZonedDateTime startOfDay() {
        return day == null ? null : day.start();
    }

    public ZonedDateTime endOfDay() {
        return day == null ? null : day.end();
    }

    public Boolean isReported() {
        return searchOption == null ? null : searchOption.getIsReported();
    }

    public Sort sort() {
        return pageable == null ? null : pageable().getSort();
    }
}
