package com.jocoos.mybeautip.post;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.member.Member;

@NoArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "post_comments")
public class PostComment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long postId;

  @Column(nullable = false)
  private String comment;

  @Column
  private Long parentId;

  @Column
  private int commentCount;

  @Column
  private int likeCount;

  @ManyToOne(optional = false)
  @JoinColumn(name = "created_by")
  private Member createdBy;

  @Column(nullable = false)
  @CreatedDate
  private Date createdAt;

  @Column(nullable = false)
  @LastModifiedDate
  private Date modifiedAt;

  public PostComment(Long postId, Member createdBy) {
    this.postId = postId;
    this.createdBy = createdBy;
  }
}
