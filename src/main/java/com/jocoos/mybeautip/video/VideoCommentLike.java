package com.jocoos.mybeautip.video;

import javax.persistence.*;
import java.util.Date;

import com.jocoos.mybeautip.member.Member;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@NoArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "video_comment_likes")
public class VideoCommentLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "comment_id")
  private VideoComment comment;

  @ManyToOne
  @JoinColumn(name = "created_by")
  private Member createdBy;

  @Column(nullable = false)
  @CreatedDate
  private Date createdAt;

  public VideoCommentLike(VideoComment comment, Member createdBy) {
    this.comment = comment;
    this.createdBy = createdBy;
  }
}