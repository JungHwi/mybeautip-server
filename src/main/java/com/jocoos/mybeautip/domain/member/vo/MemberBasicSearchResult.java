package com.jocoos.mybeautip.domain.member.vo;

import com.jocoos.mybeautip.domain.member.code.GrantType;
import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.persistence.domain.MemberActivityCount;
import com.jocoos.mybeautip.member.Member;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class MemberBasicSearchResult {
    private final Long id;
    private final MemberStatus status;
    private final String avatarUrl;
    private final String username;
    private final String email;
    private final GrantType grantType;
    private final int point;
    private final int communityCount;
    private final int commentCount;
    private final Boolean isPushable;
    private final ZonedDateTime createdAt;
    private final ZonedDateTime modifiedAt;
    private int reportCount;
    private Boolean isAgreeMarketingTerm;

    @QueryProjection
    public MemberBasicSearchResult(Member member, MemberActivityCount activityCount) {
        this.id = member.getId();
        this.status = member.getStatus();
        this.avatarUrl = member.getAvatarUrl();
        this.username = member.getUsername();
        this.email = member.getEmail();
        this.point = member.getPoint();
        this.isPushable = member.getPushable();
        this.grantType = member.getGrantType();
        this.createdAt = member.getCreatedAtZoned();
        this.modifiedAt = member.getModifiedAtZoned();
        this.communityCount = activityCount.getCommunityCount();
        this.commentCount = activityCount.getTotalNormalCommentCount();
    }

    public static void setIsAgreeMarketingTerm(List<MemberBasicSearchResult> contents, Map<Long, Boolean> agreeTermMap) {
        for (MemberBasicSearchResult content : contents) {
            content.setIsAgreeMarketingTerm(agreeTermMap.getOrDefault(content.getId(), false));
        }
    }

    public static List<Long> memberIds(List<MemberBasicSearchResult> contents) {
        return contents
                .stream()
                .map(MemberBasicSearchResult::getId)
                .toList();
    }

    public void setIsAgreeMarketingTerm(boolean isAgreeMarketingTerm) {
        this.isAgreeMarketingTerm = isAgreeMarketingTerm;
    }

    public void setReportCount(Integer reportCount) {
        this.reportCount = reportCount;
    }
}
