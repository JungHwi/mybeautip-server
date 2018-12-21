package com.jocoos.mybeautip.admin;

import java.util.Date;

import org.springframework.data.rest.core.config.Projection;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.video.Video;

@Projection(name = "video_details", types = Video.class)
public interface VideoExceprt {

  Long getId();

  String getVideoKey();

  public String getType();

  public String getState();

  public String getVisibility();

  public String getTitle();

  public String getContent();

  public String getUrl();

  public String getThumbnailPath();

  public String getThumbnailUrl();

  public String getChatRoomId();

  public int getDuration();

  public String getData();

  public Integer getWatchCount();

  public Integer getTotalWatchCount();

  public Integer getHeartCount();

  public Integer getViewCount();

  public Integer getLikeCount();

  public Integer getCommentCount();

  public Integer getOrderCount();

  public Integer getRelatedGoodsCount();

  public String getRelatedGoodsThumbnailUrl();

  public Member getMember();

  public String getTagInfo();

  public Date getCreatedAt();

  public Date getModifiedAt();

  public Date getDeletedAt();
}
