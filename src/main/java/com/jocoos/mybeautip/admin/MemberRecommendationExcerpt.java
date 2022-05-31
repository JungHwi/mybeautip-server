package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.recommendation.MemberRecommendation;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(name = "recommended_member", types = MemberRecommendation.class)
public interface MemberRecommendationExcerpt {

    Member getMember();

    int getSeq();

    Date getCreatedAt();

    Date getStartedAt();

    Date getEndedAt();

    Member getCreatedBy();
}
