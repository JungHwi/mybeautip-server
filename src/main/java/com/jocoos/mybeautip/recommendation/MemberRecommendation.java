package com.jocoos.mybeautip.recommendation;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.member.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "recommended_members")
public class MemberRecommendation extends MemberAuditable {

  @Id
  @Column(name = "member_id")
  private Long memberId;

  @MapsId
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "member_id")
  private Member member;

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