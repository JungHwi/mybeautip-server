package com.jocoos.mybeautip.admin;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.video.Video;
import org.springframework.data.rest.core.config.Projection;

import java.util.Date;

@Projection(name = "video_details", types = Video.class)
public interface VideoExceprt {

    Long getId();

    String getVideoKey();

    String getType();

    String getState();

    String getVisibility();

    boolean getLocked();

    String getTitle();

    String getContent();

    String getUrl();

    String getOriginalFilename();

    String getThumbnailPath();

    String getThumbnailUrl();

    String getChatRoomId();

    int getDuration();

    String getData();

    Integer getWatchCount();

    Integer getTotalWatchCount();

    Integer getHeartCount();

    Integer getViewCount();

    Integer getLikeCount();

    Integer getCommentCount();

    Integer getOrderCount();

    Integer getReportCount();

    Integer getRelatedGoodsCount();

    String getRelatedGoodsThumbnailUrl();

    Member getMember();

    Date getCreatedAt();

    Date getModifiedAt();

    Date getDeletedAt();
}
