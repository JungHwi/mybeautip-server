package com.jocoos.mybeautip.member;

import java.util.Date;
import java.util.List;

import com.google.common.base.Strings;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.restapi.VideoController;

@NoArgsConstructor
@Data
public class MemberInfo {
  private Long id;
  private String username;
  private String email;
  private String avatarUrl;
  private String intro;
  private Integer followerCount;
  private Integer followingCount;
  private Long followingId;
  private Integer videoCount;
  private List<VideoController.VideoInfo> videos;
  private Date createdAt;
  private Date modifiedAt;
  private Date deletedAt;

  public MemberInfo(Member member, Long followingId) {
    this.id = member.getId();
    this.username = member.getUsername();
    this.email = member.getEmail();
    this.avatarUrl = Strings.isNullOrEmpty(member.getAvatarUrl()) ? "" : member.getAvatarUrl();
    this.intro = Strings.isNullOrEmpty(member.getIntro()) ? "" : member.getIntro();
    this.createdAt = member.getCreatedAt();
    this.modifiedAt = member.getModifiedAt();
    this.deletedAt = member.getDeletedAt();
    this.followerCount = (member.getFollowerCount() < 0) ? 0 : member.getFollowerCount();
    this.followingCount = (member.getFollowingCount() < 0) ? 0 : member.getFollowingCount();
    this.videoCount = (member.getVideoCount() < 0) ? 0 : member.getVideoCount();
    this.followingId = followingId;
  }
}
