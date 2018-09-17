package com.jocoos.mybeautip.recommendation;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.tag.Tag;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "keyword_recommendations")
public class KeywordRecommendation extends MemberAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * 1: member, 2: tag
   */
  @Column(nullable = false)
  private int category;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne
  @JoinColumn(name = "tag_id")
  private Tag tag;

  @Column(nullable = false)
  private int seq;

  @Column
  @LastModifiedDate
  private Date modifiedAt;

  @Column
  private Date startedAt;

  @Column
  private Date endedAt;
}