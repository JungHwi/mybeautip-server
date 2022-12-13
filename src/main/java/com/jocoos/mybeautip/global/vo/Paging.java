package com.jocoos.mybeautip.global.vo;

import com.jocoos.mybeautip.global.code.PagingType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.code.PagingType.*;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Paging {

    private final PagingType type;
    private final Pageable pageable;
    private final ZonedDateTime cursor;

    public static Paging offsetBased(Integer page, Integer size) {
        if (page == null || size == null) {
            return noPaging();
        }
        return new Paging(OFFSET, PageRequest.of(page, size), null);
    }

    public static Paging cursorBased(ZonedDateTime cursor, Integer size) {
        if (size == null) {
            return noPaging();
        }
        return new Paging(CURSOR, PageRequest.ofSize(size), cursor);
    }

    private static Paging noPaging() {
        return new Paging(NO_PAGING,null, null);
    }

    public long getOffset() {
        return pageable.getOffset();
    }

    public long getSize() {
        return pageable.getPageSize();
    }
}
