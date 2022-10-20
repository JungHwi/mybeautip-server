package com.jocoos.mybeautip.domain.event.vo;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Paging {
    private final Long page;
    private final Long size;

    public Paging(long size) {
        this.page = null;
        this.size = size;
    }

    public long getOffset() {
        return page == null ? 0 : page * size;
    }

    public boolean isNoOffset() {
        return page == null;
    }
}
