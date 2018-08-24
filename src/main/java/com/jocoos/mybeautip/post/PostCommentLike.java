package com.jocoos.mybeautip.post;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.member.Member;

@NoArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "post_comment_likes")
public class PostCommentLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "post_comment")
  private PostComment postComment;

  @ManyToOne
  @JoinColumn(name = "created_by")
  private Member createdBy;

  @Column(nullable = false)
  @CreatedDate
  private Date createdAt;

  public PostCommentLike(PostComment postComment, Member createdBy) {
    this.postComment = postComment;
    this.createdBy = createdBy;
  }
}
