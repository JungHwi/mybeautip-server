package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.report.Report;
import com.jocoos.mybeautip.recommendation.MemberRecommendation;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(name = "member_detail", types = {Member.class, MemberRecommendation.class, Report.class})
public interface MemberExcerpt {

    Long getId();

    boolean isVisible();

    String getUsername();

    String getEmail();

    int getPoint();

    String getIntro();

    int getLink();

    int getFollowerCount();

    int getFollowingCount();

    int getPublicVideoCount();

    int getTotalVideoCount();

    int getOrderCount();

    int getReportCount();

    int getRevenue();

    MemberInfo.PermissionInfo getPermission();

    Date getRevenueModifiedAt();

    Date getCreatedAt();

    Date getDeletedAt();

    Date getModifiedAt();
}
