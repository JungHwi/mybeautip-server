package com.jocoos.mybeautip.video;

import javax.persistence.*;
import java.util.Date;

import com.jocoos.mybeautip.member.Member;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@NoArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "video_comments")
public class VideoComment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long videoId;

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

  @Column
  @LastModifiedDate
  private Date modifiedAt;

  public VideoComment(Long videoId, Member createdBy) {
    this.videoId = videoId;
    this.createdBy = createdBy;
  }
}