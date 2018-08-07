package com.jocoos.mybeautip.banner;

import javax.persistence.*;

import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "banners")
public class Banner {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private String description;

  @Column(nullable = false)
  private String thumbnailUrl;

  /**
   * 1: post(trend), 2: goods(goods list), 3: events, 4: video
   */
  @Column(nullable = false)
  private int category;

  @Column(nullable = false)
  private int seq;

  @Column(nullable = false)
  private Long viewCount = 0l;

  @Column(nullable = false)
  private String link;

  @Column
  private Date startedAt;

  @Column
  private Date endedAt;

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
  private Date deletedAt;
}
