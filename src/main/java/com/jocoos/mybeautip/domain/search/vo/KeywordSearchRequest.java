package com.jocoos.mybeautip.domain.search.vo;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import lombok.Builder;

import javax.annotation.Nullable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;

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
        if (isBlank(cursor)) {
            return null;
        }

        try {
            return Long.parseLong(cursor);
        } catch (NumberFormatException e) {
            throw new BadRequestException("Wrong Cursor Format. Need To Be Number. Request Cursor : " + cursor);
        }
    }

    @Nullable
    public ZonedDateTime dateCursor() {
        if (isBlank(cursor)) {
            return null;
        }

        try {
            return ZonedDateTime.parse(cursor, ZONE_DATE_TIME_MILLI_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Wrong Cursor Format. Need To Be Date Time. Request Cursor : " + cursor);
        }
    }
}
