package com.jocoos.mybeautip.admin;

import org.springframework.beans.BeanUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.goods.GoodsInfo;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.recommendation.MotdRecommendation;
import com.jocoos.mybeautip.restapi.VideoController;
import com.jocoos.mybeautip.video.Video;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class MotdDetailInfo extends VideoController.VideoInfo {
  private MotdRecommendation recommendation;
  private Long reportCount;
  private MemberInfo member;

  public MotdDetailInfo(Video video) {
    BeanUtils.copyProperties(video, this);
    this.member = new MemberInfo(video.getMember());
  }

  public MotdDetailInfo(Video video, MotdRecommendation recommendation) {
    this(video);
    this.recommendation = recommendation;
  }
}