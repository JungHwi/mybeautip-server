package com.jocoos.mybeautip.admin;

import java.util.Date;

import org.springframework.data.rest.core.config.Projection;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.recommendation.KeywordRecommendation;
import com.jocoos.mybeautip.tag.Tag;

@Projection(name = "keyword_details", types = KeywordRecommendation.class)
public interface KeywordExcerpt {

  Long getId();

  int getCategory();

  Member getMember();

  Tag getTag();

  int getSeq();

  Date getCreatedAt();

  Date getModifiedAt();

  Date getStartedAt();

  Date getEndedAt();

  Member getCreatedBy();
}
