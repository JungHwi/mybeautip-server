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
  private String videoKey;

  @Column
  private int commentCount;

  @Column
  private int relatedGoodsCount;

  @Column
  private String relatedGoodsThumbnailUrl;

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

  public Video(String videoKey, Member member, int goodsCount, String goodsThumbnailUrl) {
    this.videoKey = videoKey;
    this.member = member;
    this.relatedGoodsCount = goodsCount;
    this.relatedGoodsThumbnailUrl = (goodsThumbnailUrl == null) ? "" : goodsThumbnailUrl;
  }
}