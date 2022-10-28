package com.jocoos.mybeautip.global.vo;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Paging {
    private final Long page;
    private final Long size;

    private final ZonedDateTime cursor;

    public static Paging onlyLimit(long size) {
        return new Paging(null, size, null);
    }

    public static Paging page(Long page, Long size) {
        return new Paging(page, size, null);
    }

    public static Paging noOffset(ZonedDateTime cursor, Long size) {
        return new Paging(null, size, cursor);
    }

    public long getOffset() {
        return page == null ? 0 : page * size;
    }

    public boolean isNoOffset() {
        return page == null;
    }
}
