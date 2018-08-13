package com.jocoos.mybeautip.video;

import javax.persistence.*;
import java.util.Date;

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
@Table(name = "videos")
public class Video {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column
  private Long videoKey;

  @Column
  private String type;

  @Column
  private String thumbnailUrl;

  @Column
  private int commentCount;

  @ManyToOne
  @JoinColumn(name = "owner")
  private Member member;

  @Column
  @CreatedDate
  public Date createdAt;

  @Column
  @LastModifiedDate
  public Date modifiedAt;

  @Column
  public Date deletedAt;

  public Video(Long videoKey, String type, String thumbnailUrl, Member member) {
    this.videoKey = videoKey;
    this.type = type;
    this.thumbnailUrl = thumbnailUrl;
    this.member = member;
  }
}