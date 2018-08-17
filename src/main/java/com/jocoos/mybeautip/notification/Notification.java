package com.jocoos.mybeautip.notification;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;

import com.jocoos.mybeautip.member.Member;

@Data
@EntityListeners(AuditingEntityListener.class)
@Entity
@Table(name = "notifications")
public class Notification {

  public static final String VIDEO_STARTED = "notification.video_started";
  public static final String MENTION = "notification.mention";
  public static final String VIDEO_COMMENT = "notification.video_comment";
  public static final String COMMENT_REPLY = "notification.comment_reply";
  public static final String FOLLOWING = "notification.following";
  public static final String VIDEO_LIKE = "notification.video_like";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "source_member")
  private Member sourceMember;

  @ManyToOne
  @JoinColumn(name = "target_member")
  private Member targetMember;

  @Column(length = 50)
  private String type;

  @Column
  private boolean read;

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
  @Column(name = "goods_no")
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

}
