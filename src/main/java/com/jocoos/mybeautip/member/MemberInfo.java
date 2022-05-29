package com.jocoos.mybeautip.member;

import com.jocoos.mybeautip.domain.member.dto.MemberDetailResponse;
import com.jocoos.mybeautip.restapi.VideoController;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Data
public class MemberInfo {
  private Long id;
  private String tag;
  private String username;
  private String email;
  private String phoneNumber;
  private String avatarUrl;
  private PermissionInfo permission;
  private Integer followerCount;
  private Integer followingCount;
  private Long followingId;
  private Long reportedId;
  private Long blockedId;
  private Integer videoCount;
  private List<VideoController.VideoInfo> videos;
  private MemberDetailResponse memberDetail;
  private Date createdAt;
  private Date modifiedAt;
  private Date deletedAt;

  public MemberInfo(Member member) {
    this.id = member.getId();
    this.tag = member.getTag();
    this.username = StringUtils.isBlank(member.getUsername()) ? "" : member.getUsername();
    this.email = StringUtils.isBlank(member.getEmail()) ? "" : member.getEmail();
    this.phoneNumber = StringUtils.isBlank(member.getPhoneNumber()) ? "" : member.getPhoneNumber();
    this.avatarUrl = StringUtils.isBlank(member.getAvatarUrl()) ? "" : member.getAvatarUrl();
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

  public MemberInfo(Member member, MemberExtraInfo extraInfo) {
    this(member);
    if (extraInfo != null) {
      this.followingId = extraInfo.getFollowingId();
      this.reportedId = extraInfo.getReportedId();
      this.blockedId = extraInfo.getBlockedId();
    }
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
