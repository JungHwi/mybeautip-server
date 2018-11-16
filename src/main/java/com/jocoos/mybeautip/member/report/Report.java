package com.jocoos.mybeautip.member.report;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.member.Member;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Slf4j
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "member_reports")
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