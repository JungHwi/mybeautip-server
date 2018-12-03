package com.jocoos.mybeautip.admin;

import org.springframework.beans.BeanUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.recommendation.MotdRecommendation;
import com.jocoos.mybeautip.restapi.VideoController;
import com.jocoos.mybeautip.video.Video;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class MotdDetailInfo extends VideoController.VideoInfo {
  private MotdRecommendation recommendation;
  private Long reportCount;

  public MotdDetailInfo(Video video) {
    BeanUtils.copyProperties(video, this);
  }

  public MotdDetailInfo(Video video, MotdRecommendation recommendation) {
    this(video);
    this.recommendation = recommendation;
  }
}