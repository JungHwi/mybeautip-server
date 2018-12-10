package com.jocoos.mybeautip.notification;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.CommentLike;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.post.Post;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.video.VideoLike;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
@Data
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "notifications")
public class Notification {

  public static final String INSTANT = "instant";
  public static final String MY_VIDEO_UPLOADED= "my_video_uploaded";

  public static final String FOLLOWING = "following";
  public static final String VIDEO_STARTED = "video_started";
  public static final String VIDEO_UPLOADED = "video_uploaded";
  public static final String VIDEO_LIKE = "video_like";
  public static final String COMMENT = "comment";
  public static final String COMMENT_REPLY = "comment_reply";
  public static final String COMMENT_LIKE = "comment_like";
  public static final String MENTION = "mention";
  
  private static final String RESOURCE_TYPE_MEMBER = "member";
  private static final String RESOURCE_TYPE_VIDEO = "video";
  private static final String RESOURCE_TYPE_VIDEO_COMMENT = "video_comment";
  private static final String RESOURCE_TYPE_POST_COMMENT = "post_comment";
  private static final String RESOURCE_TYPE_VIDEO_COMMENT_REPLY = "video_comment_reply";
  private static final String RESOURCE_TYPE_POST_COMMENT_REPLY = "post_comment_reply";

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
  private String resourceIds;

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

  @Transient
  private String instantMessage;

  public Notification(Following following, Long followId) {
    this.type = FOLLOWING;
    this.targetMember = following.getMemberYou();
    this.read = false;
    this.resourceType = RESOURCE_TYPE_MEMBER;
    this.resourceId = following.getMemberMe().getId();
    this.resourceIds = StringUtils.joinWith(",", following.getMemberMe().getId());
    this.resourceOwner = following.getMemberMe();
    this.imageUrl = following.getMemberMe().getAvatarUrl();
    this.args = Lists.newArrayList(following.getMemberMe().getUsername());
    if (followId != null) {
      custom = Maps.newHashMap();
      custom.put("follow_id", String.valueOf(followId));
    }
  }

  public Notification(Video video, String thumbnail, Member target) {
    this.type = "broadcasted".equalsIgnoreCase(video.getType()) ? VIDEO_STARTED : VIDEO_UPLOADED;
    this.targetMember = target;
    this.args = Lists.newArrayList(video.getMember().getUsername());
    this.resourceType = RESOURCE_TYPE_VIDEO;
    this.resourceId = video.getId();
    this.resourceIds = StringUtils.joinWith(",", video.getId());
    this.resourceOwner = video.getMember();
    this.imageUrl = thumbnail;
  }

  public Notification(Video video) {
    this.type = MY_VIDEO_UPLOADED;
    this.targetMember = video.getMember();
    this.args = Lists.newArrayList(video.getMember().getUsername());
    this.resourceType = RESOURCE_TYPE_VIDEO;
    this.resourceId = video.getId();
    this.resourceIds = StringUtils.joinWith(",", video.getId());
    this.resourceOwner = video.getMember();
    this.imageUrl = video.getThumbnailUrl();
  }

  
  public Notification(VideoLike videoLike, Member source) {
    this.type = VIDEO_LIKE;
    this.targetMember = videoLike.getVideo().getMember();
    this.read = false;
    this.resourceType = RESOURCE_TYPE_VIDEO;
    this.resourceId = videoLike.getVideo().getId();
    this.resourceIds = StringUtils.joinWith(",", videoLike.getVideo().getId());
    this.resourceOwner = source;
    this.imageUrl = videoLike.getVideo().getThumbnailUrl();
    this.args = Lists.newArrayList(source.getUsername());
  }
  
  public Notification(Video video, Comment comment, Member target, String thumbnail) {
    this.type = COMMENT;
    this.targetMember = target;
    this.read = false;
    this.resourceType = RESOURCE_TYPE_VIDEO_COMMENT;
    this.resourceId = comment.getId();
    this.resourceIds = StringUtils.joinWith(",", video.getId(), comment.getId());
    this.resourceOwner = comment.getCreatedBy();
    this.imageUrl = thumbnail;
    this.args = Lists.newArrayList(comment.getCreatedBy().getUsername(), comment.getComment());
  }
  
  public Notification(Post post, Comment comment, Member target, String thumbnail) {
    this.type = COMMENT;
    this.targetMember = target;
    this.read = false;
    this.resourceType = RESOURCE_TYPE_POST_COMMENT;
    this.resourceId = comment.getId();
    this.resourceIds = StringUtils.joinWith(",", post.getId(), comment.getId());
    this.imageUrl = thumbnail;
    this.args = Lists.newArrayList(comment.getCreatedBy().getUsername(), comment.getComment());
  }

  public Notification(Video video, Comment comment, Long parentId, Member target, String thumbnail) {
    this.type = COMMENT_REPLY;
    this.targetMember = target;
    this.read = false;
    this.resourceType = RESOURCE_TYPE_VIDEO_COMMENT_REPLY;
    this.resourceId = comment.getId();
    this.resourceIds = StringUtils.joinWith(",", video.getId(), comment.getParentId(), comment.getId());
    this.resourceOwner = comment.getCreatedBy();
    this.imageUrl = thumbnail;
    this.args = Lists.newArrayList(comment.getCreatedBy().getUsername(), comment.getComment());
  }
  
  public Notification(Post post, Comment comment, Long parentId, Member target, String thumbnail) {
    this.type = COMMENT_REPLY;
    this.targetMember = target;
    this.read = false;
    this.resourceType = RESOURCE_TYPE_POST_COMMENT_REPLY;
    this.resourceId = comment.getId();
    this.resourceIds = StringUtils.joinWith(",", post.getId(), comment.getParentId(), comment.getId());
    this.resourceOwner = comment.getCreatedBy();
    this.imageUrl = thumbnail;
    this.args = Lists.newArrayList(comment.getCreatedBy().getUsername(), comment.getComment());
  }

  public Notification(Video video, CommentLike commentLike, String commentStr) {
    this.type = COMMENT_LIKE;
    this.targetMember = commentLike.getComment().getCreatedBy();
    this.read = false;
    this.resourceType = RESOURCE_TYPE_VIDEO_COMMENT;
    this.resourceId = commentLike.getComment().getId();
    this.resourceIds = StringUtils.joinWith(",", video.getId(), commentLike.getComment().getId());
    this.resourceOwner = commentLike.getCreatedBy();
    this.imageUrl = video.getThumbnailUrl();
    this.args = Lists.newArrayList(commentLike.getCreatedBy().getUsername(), commentStr);
  }
  
  public Notification(Post post, CommentLike commentLike, String commentStr) {
    this.type = COMMENT_LIKE;
    this.targetMember = commentLike.getComment().getCreatedBy();
    this.read = false;
    this.resourceType = RESOURCE_TYPE_POST_COMMENT;
    this.resourceId = commentLike.getComment().getId();
    this.resourceIds = StringUtils.joinWith(",", post.getId(), commentLike.getComment().getId());
    this.resourceOwner = commentLike.getCreatedBy();
    this.imageUrl = post.getThumbnailUrl();
    this.args = Lists.newArrayList(commentLike.getCreatedBy().getUsername(), commentStr);
  }

  public Notification(Post post, Comment postComment, Member mentioned) {
    this.type = MENTION;
    this.targetMember = mentioned;
    this.read = false;
    this.resourceType = RESOURCE_TYPE_POST_COMMENT;
    this.resourceId = postComment.getId();
    this.resourceIds = StringUtils.joinWith(",", post.getId(), postComment.getId());
    this.resourceOwner = postComment.getCreatedBy();
    this.imageUrl = post.getThumbnailUrl();
    this.args = Lists.newArrayList(postComment.getCreatedBy().getUsername(), postComment.getComment());
  }

  public Notification(Video video, Comment videoComment, Member mentioned) {
    this.type = MENTION;
    this.targetMember = mentioned;
    this.read = false;
    this.resourceType = RESOURCE_TYPE_VIDEO_COMMENT;
    this.resourceId = videoComment.getId();
    this.resourceIds = StringUtils.joinWith(",", video.getId(), videoComment.getId());
    this.resourceOwner = videoComment.getCreatedBy();
    this.imageUrl = video.getThumbnailUrl();
    this.args = Lists.newArrayList(videoComment.getCreatedBy().getUsername(), videoComment.getComment());
  }

  public Notification(Member target, String message) {
    this.type = INSTANT;
    this.targetMember = target;
    this.instantMessage = message;
  }
}
