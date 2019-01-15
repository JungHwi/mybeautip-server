package com.jocoos.mybeautip.admin;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.data.rest.core.config.Projection;

import com.jocoos.mybeautip.banner.Banner;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.post.PostContent;

@Projection(name = "post_details", types = Post.class)
public interface PostExcerpt {

  Long getId();

  String getTitle();

  String getDescription();

  String getThumbnailUrl();

  int getCategory();

  int getProgress();

  int getViewCount();

  int getLikeCount();

  int getCommentCount();

  Banner getBanner();

  Set<PostContent> getContents();

  Set<Long> getWinners();

  List<String> getGoods();

  String getTagInfo();

  Member getCreatedBy();

  boolean isOpened();

  Date getStartedAt();

  Date getEndedAt();

  Date getCreatedAt();

  Date getModifiedAt();

  Date getDeletedAt();
}
