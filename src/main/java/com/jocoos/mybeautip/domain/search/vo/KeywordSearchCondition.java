package com.jocoos.mybeautip.domain.search.vo;

import com.jocoos.mybeautip.member.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Date;


@Getter
@RequiredArgsConstructor
public class KeywordSearchCondition {

    private final String keyword;

    private final Long memberId;
    private final ZonedDateTime cursor;

    private final int size;

    public String getKeyword() {
        return keyword.trim();
    }

    public Date cursorDate() {
        return cursor == null ? null : Date.from(cursor.toInstant());
    }

    public KeywordSearchCondition(String keyword, Member member, ZonedDateTime cursor, int size) {
        this.keyword = keyword;
        this.memberId = member == null ? null : member.getId();
        this.cursor = cursor;
        this.size = size;
    }
}
