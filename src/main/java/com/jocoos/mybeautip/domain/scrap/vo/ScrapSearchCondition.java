package com.jocoos.mybeautip.domain.scrap.vo;

import com.jocoos.mybeautip.domain.scrap.code.ScrapType;
import lombok.Builder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Builder
public record ScrapSearchCondition(ScrapType type,
                                   Long memberId,
                                   Boolean isScrap,
                                   Long cursor,
                                   Pageable pageable) {

    public long limit() {
        return pageable.getPageSize();
    }

    public Sort sort() {
        return pageable.getSort();
    }
}
