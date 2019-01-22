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

  @Column(nullable = false)
  private String videoKey;

  @Column(nullable = false)
  private String type;

  @Column(nullable = false)
  private String state;
  
  @Column(nullable = false)
  private Boolean locked;
  
  @Column(nullable = false)
  private Boolean muted;

  @Column(nullable = false)
  private String visibility;

  @Column
  private String title;

  @Column
  private String content;

  @Column
  private String url;

  @Column
  private String thumbnailPath;

  @Column
  private String thumbnailUrl;

  @Column
  private String chatRoomId;

  @Column(nullable = false)
  private int duration;

  @Column
  private String data;

  @Column(nullable = false)
  private Integer watchCount;

  @Column(nullable = false)
  private Integer totalWatchCount;

  @Column(nullable = false)
  private Integer heartCount;

  @Column(nullable = false)
  private Integer viewCount;

  @Column(nullable = false)
  private Integer likeCount;

  @Column(nullable = false)
  private Integer commentCount;

  @Column(nullable = false)
  private Integer orderCount;
  
  @Column
  private Integer reportCount;

  @Column(nullable = false)
  private Integer relatedGoodsCount;

  @Column(nullable = false)
  private String relatedGoodsThumbnailUrl;

  @ManyToOne
  @JoinColumn(name = "owner")
  private Member member;
  
  @Column
  private String tagInfo;

  @Column
  @CreatedDate
  public Date createdAt;

  @Column
  @LastModifiedDate
  public Date modifiedAt;

  @Column
  public Date deletedAt;
  
  public Video(Member owner) {
    this.member = owner;
    this.videoKey = "";
    this.state = "CREATED";
    this.url = "";
    this.thumbnailPath = "";
    this.thumbnailUrl = "";
    this.relatedGoodsCount = 0;
    this.relatedGoodsThumbnailUrl = "";
    this.commentCount = 0;
    this.heartCount = 0;
    this.likeCount = 0;
    this.watchCount = 0;
    this.totalWatchCount = 0;
    this.viewCount = 0;
    this.orderCount = 0;
  }
}