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
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentLike;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoLike;

@NoArgsConstructor
@Data
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "notifications")
public class Notification {

  public static final String VIDEO_STARTED = "video_started";
  public static final String VIDEO_UPLOADED = "video_uploaded";
  public static final String MENTION = "mention";
  public static final String COMMENT = "comment";
  public static final String COMMENT_REPLY = "comment_reply";
  public static final String COMMENT_LIKE = "comment_like";
  public static final String FOLLOWING = "following";
  public static final String VIDEO_LIKE = "video_like";

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

  @ManyToOne(optional = false)
  @JoinColumn(name = "resource_owner")
  private Member resourceOwner;

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
    this.type = "broadcasted".equalsIgnoreCase(video.getType()) ? VIDEO_STARTED : VIDEO_UPLOADED;
    this.targetMember = target;
    this.args = Lists.newArrayList(video.getMember().getUsername());
    this.resourceType = "video";
    this.resourceId = video.getId();
    this.resourceOwner = video.getMember();
    this.imageUrl = thumbnail;
  }

  public Notification(Following following, Long followId) {
    this.type = FOLLOWING;
    this.targetMember = following.getMemberYou();
    this.read = false;
    this.resourceType = "member";
    this.resourceId = following.getMemberMe().getId();
    this.resourceOwner = following.getMemberMe();
    this.imageUrl = following.getMemberMe().getAvatarUrl();
    this.args = Lists.newArrayList(following.getMemberMe().getUsername());
    if (followId != null) {
      custom = Maps.newHashMap();
      custom.put("follow_id", String.valueOf(followId));
    }
  }

  public Notification(Comment comment, Member target, String thumbnail) {
    this.type = COMMENT;
    this.targetMember = target;
    this.read = false;
    this.resourceType = "comment";
    this.resourceId = comment.getId();
    this.resourceOwner = comment.getCreatedBy();
    this.imageUrl = thumbnail;
    this.args = Lists.newArrayList(comment.getCreatedBy().getUsername(), comment.getComment());
  }

  public Notification(Comment comment, Long parentId, Member target, String thumbnail) {
    this.type = COMMENT_REPLY;
    this.targetMember = target;
    this.read = false;
    this.resourceType = "comment_reply";
    this.resourceId = comment.getId();
    this.resourceOwner = comment.getCreatedBy();
    this.imageUrl = thumbnail;
    this.args = Lists.newArrayList(comment.getCreatedBy().getUsername(), comment.getComment());
  }

  public Notification(CommentLike commentLike, String thumbnail) {
    this.type = COMMENT_LIKE;
    this.targetMember = commentLike.getComment().getCreatedBy();
    this.read = false;
    this.resourceType = "comment_like";
    this.resourceId = commentLike.getId();
    this.resourceOwner = commentLike.getCreatedBy();
    this.imageUrl = thumbnail;
    this.args = Lists.newArrayList(commentLike.getCreatedBy().getUsername(), commentLike.getComment().getComment());
  }

  public Notification(VideoLike videoLike, Member source) {
    this.type = VIDEO_LIKE;
    this.targetMember = videoLike.getVideo().getMember();
    this.read = false;
    this.resourceType = "video_like";
    this.resourceId = videoLike.getId();
    this.resourceOwner = source;
    this.imageUrl = videoLike.getVideo().getThumbnailUrl();
    this.args = Lists.newArrayList(source.getUsername());
  }
}
