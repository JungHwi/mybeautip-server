package com.jocoos.mybeautip.member.report;

import javax.persistence.*;
import java.util.Date;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.member.Member;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Slf4j
@EqualsAndHashCode(callSuper = false)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "members_reports")
public class Report extends CreatedDateAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "me")
  private Member me;

  @ManyToOne
  @JoinColumn(name = "you")
  private Member you;

  private String reason;
  
  public Report(Member me, Member you, String reason) {
    this.me = me;
    this.you = you;
    this.reason = reason;
  }
}