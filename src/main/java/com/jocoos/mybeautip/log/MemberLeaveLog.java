package com.jocoos.mybeautip.log;

import javax.persistence.*;

import com.jocoos.mybeautip.member.Member;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;

@Data
@NoArgsConstructor
@Slf4j
@EqualsAndHashCode(callSuper = false)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "member_leave_log")
public class MemberLeaveLog extends CreatedDateAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;

  @Column(nullable = false)
  private String reason;

  public MemberLeaveLog(Member member, String reason) {
    this.member = member;
    this.reason = reason;
  }
}
