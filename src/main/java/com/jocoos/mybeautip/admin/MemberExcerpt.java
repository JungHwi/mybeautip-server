package com.jocoos.mybeautip.admin;

import java.util.Date;
import java.util.List;

import org.springframework.data.rest.core.config.Projection;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.report.Report;
import com.jocoos.mybeautip.recommendation.MemberRecommendation;

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

  int getRevenue();

  MemberInfo.PermissionInfo getPermission();

  Date getRevenueModifiedAt();

  Date getCreatedAt();

  Date getDeletedAt();

  Date getModifiedAt();
}
