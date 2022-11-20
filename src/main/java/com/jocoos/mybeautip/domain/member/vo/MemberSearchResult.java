package com.jocoos.mybeautip.domain.member.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.member.code.GrantType;
import com.jocoos.mybeautip.domain.member.persistence.domain.MemberDetail;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.address.Address;
import com.jocoos.mybeautip.member.code.SkinType;
import com.jocoos.mybeautip.member.code.SkinWorry;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.Set;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_MILLI_FORMAT;

@Getter
@RequiredArgsConstructor
public class MemberSearchResult {
    private final Long id;
    private final String avatarUrl;
    private final String username;
    private final String email;
    private final String phoneNumber;
    private final int point;
    private final Boolean isPushable;
    private final GrantType grantType;
    private final Integer ageGroup;
    private final SkinType skinType;
    private final Set<SkinWorry> skinWorry;
    private final String address;

    private final String memo;

    @JsonFormat(pattern = ZONE_DATE_TIME_MILLI_FORMAT)
    private final ZonedDateTime createdAt;

    @JsonFormat(pattern = ZONE_DATE_TIME_MILLI_FORMAT)
    private final ZonedDateTime modifiedAt;

    @QueryProjection
    public MemberSearchResult(Member member, Address address, MemberDetail memberDetail, String memo) {
        this.id = member.getId();
        this.avatarUrl = member.getAvatarUrl();
        this.username = member.getUsername();
        this.email = member.getEmail();
        this.phoneNumber = member.getPhoneNumber();
        this.point = member.getPoint();
        this.isPushable = member.getPushable();
        this.grantType = member.getGrantType();
        this.ageGroup = member.getAgeGroup();
        this.createdAt = member.getCreatedAtZoned();
        this.modifiedAt = member.getModifiedAtZoned();
        this.address = address == null ? null : address.getWholeAddress();
        this.skinType = memberDetail == null ? null : memberDetail.getSkinType();
        this.skinWorry = memberDetail == null ? null : memberDetail.getSkinWorry();
        this.memo = memo;
    }

}
