package com.jocoos.mybeautip.banner;

import com.jocoos.mybeautip.audit.MemberAuditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "banners")
@EqualsAndHashCode(callSuper = false)
public class Banner extends MemberAuditable {

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
  private Long viewCount = 0L;

  @Column(nullable = false)
  private String link;

  @Column
  private Date startedAt;

  @Column
  private Date endedAt;

  @Column
  @LastModifiedDate
  private Date modifiedAt;

  @Column
  private Date deletedAt;
}
