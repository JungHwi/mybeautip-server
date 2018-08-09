package com.jocoos.mybeautip.member;

import java.util.Date;

import com.google.common.base.Strings;
import lombok.Data;
import lombok.NoArgsConstructor;

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
  private Integer videoCount;
  private Long followingId;
  private Date createdAt;
  private Date modifiedAt;

  public MemberInfo(Member member, Long followingId) {
    this.id = member.getId();
    this.username = member.getUsername();
    this.email = member.getEmail();
    this.avatarUrl = Strings.isNullOrEmpty(member.getAvatarUrl()) ? "" : member.getAvatarUrl();
    this.intro = Strings.isNullOrEmpty(member.getIntro()) ? "" : member.getIntro();
    this.createdAt = member.getCreatedAt();
    this.modifiedAt = member.getModifiedAt();
    this.followerCount = member.getFollowerCount();
    this.followingCount = member.getFollowingCount();
    this.videoCount = member.getVideoCount();
    this.followingId = followingId;
  }
}
