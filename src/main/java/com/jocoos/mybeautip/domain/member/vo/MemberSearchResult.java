package com.jocoos.mybeautip.domain.member.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.member.code.GrantType;
import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.code.Role;
import com.jocoos.mybeautip.domain.member.dto.MemoResponse;
import com.jocoos.mybeautip.domain.member.persistence.domain.MemberActivityCount;
import com.jocoos.mybeautip.domain.member.persistence.domain.MemberDetail;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.address.Address;
import com.jocoos.mybeautip.member.code.SkinType;
import com.jocoos.mybeautip.member.code.SkinWorry;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;
import static org.springframework.util.StringUtils.hasText;

@Getter
@RequiredArgsConstructor
public class MemberSearchResult {
    private final Long id;
    private final Role role;
    private final MemberStatus status;
    private final String avatarUrl;
    private final String username;
    private final String name;
    private final String email;
    private final String phoneNumber;
    private final int point;
    private final Boolean isPushable;
    private final GrantType grantType;
    private final Integer ageGroup;
    private final SkinType skinType;
    private final Set<SkinWorry> skinWorry;
    private final String address;
    private final List<MemoResponse> memo;
    private final int normalCommunityCount;
    private final int normalCommunityCommentCount;
    private final int normalVideoCommentCount;
    private final int totalCommunityCount;
    private final int totalCommunityCommentCount;
    private final int totalVideoCommentCount;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime createdAt;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime modifiedAt;

    @QueryProjection
    public MemberSearchResult(Member member, Address address, MemberDetail memberDetail, MemberActivityCount activityCount, List<MemoResponse> memoResponses) {
        this.id = member.getId();
        this.role = Role.from(member);
        this.status = member.getStatus();
        this.avatarUrl = member.getAvatarUrl();
        this.username = member.getUsername();
        this.email = member.getEmail();
        this.phoneNumber = getPhoneNumber(member, address);
        this.point = member.getPoint();
        this.isPushable = member.getPushable();
        this.grantType = member.getGrantType();
        this.ageGroup = member.getAgeGroup();
        this.createdAt = member.getCreatedAtZoned();
        this.modifiedAt = member.getModifiedAtZoned();
        this.name = address == null ? null : address.getRecipient();
        this.address = address == null ? null : address.getWholeAddress();
        this.skinType = memberDetail == null ? null : memberDetail.getSkinType();
        this.skinWorry = memberDetail == null ? null : memberDetail.getSkinWorry();
        this.memo = memoResponses;
        this.normalCommunityCount = activityCount.getCommunityCount();
        this.normalCommunityCommentCount = activityCount.getCommunityCommentCount();
        this.normalVideoCommentCount = activityCount.getVideoCommentCount();
        this.totalCommunityCount = activityCount.getAllCommunityCount();
        this.totalCommunityCommentCount = activityCount.getAllCommunityCommentCount();
        this.totalVideoCommentCount = activityCount.getAllVideoCommentCount();
    }

    public String getPhoneNumber(Member member, Address address) {
        if (hasText(member.getPhoneNumber())) {
            return member.getPhoneNumber();
        }
        return address == null ? null : address.getPhone();
    }
}
