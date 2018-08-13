package com.jocoos.mybeautip.recommendation;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.video.Video;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "motd_recommendations")
public class MotdRecommendation {
  @Id
  @Column(name = "video_id")
  private Long videoId;

  @MapsId
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "video_id")
  private Video video;

  @Column(nullable = false)
  private int seq;

  @Column(nullable = false)
  @CreatedBy
  private Long createdBy;

  @Column(nullable = false)
  @CreatedDate
  private Date createdAt;

  @Column
  @LastModifiedDate
  private Date modifiedAt;

  @Column
  private Date startedAt;

  @Column
  private Date endedAt;
}