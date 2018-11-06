package com.jocoos.mybeautip.admin;

import java.util.Date;

import org.springframework.data.rest.core.config.Projection;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.recommendation.MemberRecommendation;

@Projection(name = "recommendation_member", types = MemberRecommendation.class)
public interface MemberRecommendationExcerpt {

  Member getMember();

  int getSeq();

  Date getCreatedAt();

  Date getStartedAt();

  Date getEndedAt();

  Member getCreatedBy();
}
