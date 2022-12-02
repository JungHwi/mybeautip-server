package com.jocoos.mybeautip.admin.dto;

import com.jocoos.mybeautip.recommendation.MotdRecommendation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Date;

@Getter
@NoArgsConstructor
public class MotdRecommendationInfo {
  private Long videoId;
  private int seq;
  private Date modifiedAt;

  private Long baseId;
  private Date startedAt;
  private Date endedAt;

  public MotdRecommendationInfo(MotdRecommendation motdRecommendation) {
    BeanUtils.copyProperties(motdRecommendation, this);
  }
}
