package com.jocoos.mybeautip.admin;

import java.util.Date;

import org.springframework.data.rest.core.config.Projection;

import com.jocoos.mybeautip.banner.Banner;
import com.jocoos.mybeautip.post.Post;

@Projection(name = "banner_details", types = Banner.class)
public interface BannerExceprt {

  Long getId();

  String getTitle();

  String getDescription();

  String getThumbnailUrl();

  int getCategory();

  int getSeq();

  int getViewCount();

  String getLink();

  Post getPost();

  Date getStartedAt();

  Date getEndedAt();

  Date getCreatedAt();

  Date getModifiedAt();

  Date getDeletedAt();
}
