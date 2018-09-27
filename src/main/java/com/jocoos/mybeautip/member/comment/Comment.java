package com.jocoos.mybeautip.member.comment;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.MemberAuditable;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "comments")
public class Comment extends MemberAuditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * 1: post, 2: video
   */
  @Column(nullable = false)
  private int category;

  @Column
  private Long postId;

  @Column
  private Long videoId;

  @Column(nullable = false)
  private String comment;

  @Column
  private Long parentId;

  @Column
  private int commentCount;

  @Column
  private int likeCount;

  @Column(nullable = false)
  @LastModifiedDate
  private Date modifiedAt;
}
