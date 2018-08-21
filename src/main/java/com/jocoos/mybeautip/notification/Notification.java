package com.jocoos.mybeautip.notification;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.post.PostComment;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoComment;

@NoArgsConstructor
@Data
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "notifications")
public class Notification {

  public static final String VIDEO_STARTED = "notification.video_started";
  public static final String MENTION = "notification.mention";
  public static final String VIDEO_COMMENT = "notification.video_comment";
  public static final String VIDEO_COMMENT_REPLY = "notification.video_comment_reply";
  public static final String VIDEO_COMMENT_LIKE = "notification.video_comment_like";
  public static final String POST_COMMENT = "notification.post_comment";
  public static final String POST_COMMENT_REPLY = "notification.post_comment_reply";
  public static final String POST_COMMENT_LIKE = "notification.post_comment_like";
  public static final String FOLLOWING = "notification.following";
  public static final String VIDEO_LIKE = "notification.video_like";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(optional = false)
  @JoinColumn(name = "target_member")
  private Member targetMember;

  @Column(length = 50, nullable = false)
  private String type;

  @Column(nullable = false)
  private boolean read = false;

  @Column
  private String resourceType;

  @Column
  private Long resourceId;

  @Column
  private Long resourceOwner;

  @Column
  private String imageUrl;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
     name = "notification_args",
     joinColumns = @JoinColumn(name = "notification_id")
  )
  @OrderColumn(name = "seq")
  @Column(name = "arg")
  private List<String> args;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(
     name = "notification_customs",
     joinColumns = @JoinColumn(name = "notification_id")
  )
  @MapKeyColumn(name = "key")
  @Column(name = "value")
  private Map<String, String> custom;

  @Column
  @CreatedDate
  private Date createdAt;

  public Notification(Video video, String thumbnail, Member target) {
    this.type = VIDEO_STARTED;
    this.targetMember = target;
    this.args = Lists.newArrayList(video.getMember().getUsername());
    this.resourceType = "video";
    this.resourceId = video.getId();
    this.resourceOwner = video.getMember().getId();
    this.imageUrl = thumbnail;
  }

  public Notification(Following following, Long followId) {
    this.type = FOLLOWING;
    this.targetMember = following.getMemberYou();
    this.read = false;
    this.resourceType = "member";
    this.resourceId = following.getMemberMe().getId();
    this.resourceOwner = following.getMemberMe().getId();
    this.imageUrl = following.getMemberMe().getAvatarUrl();
    this.args = Lists.newArrayList(following.getMemberMe().getUsername());
    if (followId != null) {
      custom = Maps.newHashMap();
      custom.put("follow_id", String.valueOf(followId));
    }
  }

  public Notification(Video video, VideoComment videoComment, Member source) {
    this.type = VIDEO_COMMENT;
    this.targetMember = video.getMember();
    this.read = false;
    this.resourceType = "video_comment";
    this.resourceId = videoComment.getId();
    this.resourceOwner = videoComment.getCreatedBy();
    this.imageUrl = video.getThumbnailUrl();
    this.args = Lists.newArrayList(source.getUsername(), videoComment.getComment());
  }

  public Notification(String thumbnail, VideoComment videoComment, Member source, Member target) {
    this.type = VIDEO_COMMENT_REPLY;
    this.targetMember = target;
    this.read = false;
    this.resourceType = "video_comment";
    this.resourceId = videoComment.getId();
    this.resourceOwner = videoComment.getCreatedBy();
    this.imageUrl = thumbnail;
    this.args = Lists.newArrayList(source.getUsername(), videoComment.getComment());
  }

  public Notification(Post post, PostComment postComment, Member source) {
    this.type = POST_COMMENT;
    this.targetMember = post.getCreator();
    this.read = false;
    this.resourceType = "post_comment";
    this.resourceId = postComment.getId();
    this.resourceOwner = postComment.getCreatedBy();
    this.imageUrl = post.getThumbnailUrl();
    this.args = Lists.newArrayList(source.getUsername(), postComment.getComment());
  }

  public Notification(String thumbnail, PostComment postComment, Member source, Member target) {
    this.type = POST_COMMENT_REPLY;
    this.targetMember = target;
    this.read = false;
    this.resourceType = "post_comment";
    this.resourceId = postComment.getId();
    this.resourceOwner = postComment.getCreatedBy();
    this.imageUrl = thumbnail;
    this.args = Lists.newArrayList(source.getUsername(), postComment.getComment());
  }
}
