package com.jocoos.mybeautip.domain.placard.vo;

import com.jocoos.mybeautip.domain.placard.code.PlacardStatus;
import com.jocoos.mybeautip.global.vo.SearchOption;
import lombok.Builder;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;

@Builder
public record PlacardSearchCondition(PlacardStatus status,
                                     SearchOption searchOption,
                                     Pageable pageable) {


    public String keyword() {
        return searchOption == null ? null : searchOption.getKeyword();
    }

    public ZonedDateTime startAt() {
        return searchOption == null ? null : searchOption.getStartAt();
    }

    public ZonedDateTime endAt() {
        return searchOption == null ? null : searchOption.getEndAt();
    }

    public long offset() {
        return pageable.getOffset();
    }

    public long limit() {
        return pageable.getPageSize();
    }

    public Boolean IsTopFix() {
        return searchOption == null ? null : searchOption.getIsTopFix();
    }
}
