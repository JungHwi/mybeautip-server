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
  private String phoneNumber;
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
    this.phoneNumber = Strings.isNullOrEmpty(member.getPhoneNumber()) ? "" : member.getPhoneNumber();
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
  public static class PermissionInfo {
    private Boolean chatPost = false;
    private Boolean commentPost = false;
    private Boolean livePost = false;
    private Boolean motdPost = false;
    private Boolean revenueReturn = false;
    
    public PermissionInfo(int value) {
      if ((value & Member.CHAT_POST) == Member.CHAT_POST) {
        this.chatPost = true;
      }
      if ((value & Member.COMMENT_POST) == Member.COMMENT_POST) {
        this.commentPost = true;
      }
      if ((value & Member.LIVE_POST) == Member.LIVE_POST) {
        this.livePost = true;
      }
      if ((value & Member.MOTD_POST) == Member.MOTD_POST) {
        this.motdPost = true;
      }
      if ((value & Member.REVENUE_RETURN) == Member.REVENUE_RETURN) {
        this.revenueReturn = true;
      }
    }
  }
}
