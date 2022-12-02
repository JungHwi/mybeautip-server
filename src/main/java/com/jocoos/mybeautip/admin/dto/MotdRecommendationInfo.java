package com.jocoos.mybeautip.admin.dto;

import org.springframework.beans.BeanUtils;

import java.util.Date;

import com.jocoos.mybeautip.recommendation.MotdRecommendation;

import lombok.NoArgsConstructor;

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
