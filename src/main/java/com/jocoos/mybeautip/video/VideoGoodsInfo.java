package com.jocoos.mybeautip.video;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.member.MemberInfo;

@Data
@NoArgsConstructor
public class VideoGoodsInfo {
  private String videoKey;
  private String type;
  private String thumbnailUrl;
  private MemberInfo member;

  public VideoGoodsInfo(VideoGoods video, MemberInfo member) {
    this.videoKey = video.getVideoKey();
    this.type = video.getVideoType();
    this.thumbnailUrl = video.getThumbnailUrl();
    this.member = member;
  }
}