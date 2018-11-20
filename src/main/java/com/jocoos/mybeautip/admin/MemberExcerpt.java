package com.jocoos.mybeautip.admin;

import java.util.Date;

import org.springframework.data.rest.core.config.Projection;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.recommendation.MemberRecommendation;

@Projection(name = "member_detail", types = Member.class)
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

  int getVideoCount();

  int getTotalVideoCount();

  int getRevenue();

  Date getRevenueModifiedAt();

  Date getCreatedAt();

  Date getDeletedAt();

  MemberRecommendation getRecommendation();
}
