package com.jocoos.mybeautip.domain.member.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.community.dto.CommunityMemberResponse;
import com.jocoos.mybeautip.domain.member.persistence.domain.MemberMemo;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@RequiredArgsConstructor
public class MemoResponse {
    private final Long id;
    private final String memo;
    private final CommunityMemberResponse memberResponse;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime createdAt;

    @QueryProjection
    public MemoResponse(MemberMemo memberMemo, CommunityMemberResponse memberResponse) {
        this.id = memberMemo.getId();
        this.memo = memberMemo.getMemo();
        this.memberResponse = memberResponse;
        this.createdAt = memberMemo.getCreatedAt();
    }

    public MemoResponse(MemberMemo memberMemo) {
        this.id = memberMemo.getId();
        this.memo = memberMemo.getMemo();
        this.memberResponse = new CommunityMemberResponse(memberMemo.getCreatedBy().getId(), memberMemo.getCreatedBy().getUsername());
        this.createdAt = memberMemo.getCreatedAt();
    }
}

