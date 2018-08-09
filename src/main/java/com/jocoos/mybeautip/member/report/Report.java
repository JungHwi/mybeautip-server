package com.jocoos.mybeautip.member.report;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "members_reports")
public class Report {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long me;
  private Long you;
  private String reason;

  @Column
  @CreatedDate
  public Date createdAt;
  
  public Report(Long me, Long you, String reason) {
    this.me = me;
    this.you = you;
    this.reason = reason;
  }
}