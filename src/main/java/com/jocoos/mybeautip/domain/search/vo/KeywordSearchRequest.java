package com.jocoos.mybeautip.domain.search.vo;

import com.jocoos.mybeautip.member.Member;
import lombok.Builder;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_MILLI_FORMATTER;
import static org.apache.commons.lang3.StringUtils.isBlank;


@Builder
public record KeywordSearchRequest(String keyword,
                                   Member member,
                                   String tokenUsername,
                                   String cursor,
                                   int size) {

    @Override
    public String keyword() {
        return keyword.trim();
    }

    @Nullable
    public Long memberId() {
        return member == null ? null : member.getId();
    }

    @Nullable
    public Long idCursor() {
        return isBlank(cursor) ? null : Long.parseLong(cursor);
    }

    @Nullable
    public ZonedDateTime dateCursor() {
        return isBlank(cursor) ? null : ZonedDateTime.parse(cursor, ZONE_DATE_TIME_MILLI_FORMATTER);
    }
}
