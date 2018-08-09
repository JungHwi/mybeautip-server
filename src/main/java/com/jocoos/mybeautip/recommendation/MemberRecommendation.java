package com.jocoos.mybeautip.recommendation;

import com.jocoos.mybeautip.member.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "member_recommendations")
public class MemberRecommendation {

  @Id
  @Column(name = "member_id")
  private Long member_id;

  @MapsId
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "member_id")
  private Member member;

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