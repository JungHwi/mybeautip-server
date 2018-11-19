package com.jocoos.mybeautip.admin;

import java.util.Date;

import org.springframework.data.rest.core.config.Projection;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.recommendation.MotdRecommendation;
import com.jocoos.mybeautip.video.Video;

@Projection(name = "recommended_motd", types = MotdRecommendation.class)
public interface MotdRecommendationExcerpt {

  Video getVideo();

  int getSeq();

  Date getCreatedAt();

  Date getStartedAt();

  Date getEndedAt();

  Member getCreatedBy();
}
