package com.jocoos.mybeautip.member;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.restapi.VideoController;

@NoArgsConstructor
@Data
public class MemberInfo {
  @JsonIgnore private final int CHAT_POST = 1;
  @JsonIgnore private final int COMMENT_POST = 2;
  @JsonIgnore private final int LIVE_POST = 4;
  @JsonIgnore private final int MOTD_POST = 8;
  @JsonIgnore private final int REVENUE_RETURN = 16;
  
  private Long id;
  private String username;
  private String email;
  private String avatarUrl;
  private String intro;
  private PermissionInfo permission;
  private Integer followerCount;
  private Integer followingCount;
  private Long followingId;
  private Integer videoCount;
  private List<VideoController.VideoInfo> videos;
  private Date createdAt;
  private Date modifiedAt;
  private Date deletedAt;

  public MemberInfo(Member member) {
    this.id = member.getId();
    this.username = member.getUsername();
    this.email = member.getEmail();
    this.avatarUrl = Strings.isNullOrEmpty(member.getAvatarUrl()) ? "" : member.getAvatarUrl();
    this.intro = Strings.isNullOrEmpty(member.getIntro()) ? "" : member.getIntro();
    this.createdAt = member.getCreatedAt();
    this.modifiedAt = member.getModifiedAt();
    this.deletedAt = member.getDeletedAt();
    this.followerCount = member.getFollowerCount();
    this.followingCount = member.getFollowingCount();
    this.videoCount = member.getPublicVideoCount();
    this.permission = new PermissionInfo(member.getPermission());
  }

  public MemberInfo(Member member, Long followingId) {
    this(member);
    this.followingId = followingId;
  }
  
  @Data
  @NoArgsConstructor
  public class PermissionInfo {
    private Boolean chatPost = false;
    private Boolean commentPost = false;
    private Boolean livePost = false;
    private Boolean motdPost = false;
    private Boolean revenueReturn = false;
    
    public PermissionInfo(int value) {
      if ((value & CHAT_POST) == CHAT_POST) {
        this.chatPost = true;
      }
      if ((value & COMMENT_POST) == COMMENT_POST) {
        this.commentPost = true;
      }
      if ((value & LIVE_POST) == LIVE_POST) {
        this.livePost = true;
      }
      if ((value & MOTD_POST) == MOTD_POST) {
        this.motdPost = true;
      }
      if ((value & REVENUE_RETURN) == REVENUE_RETURN) {
        this.revenueReturn = true;
      }
    }
  }
}
