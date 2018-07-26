package com.jocoos.mybeautip.member.report;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@Table(name = "members_reports")
@NoArgsConstructor
public class Report extends CreatedDateAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long me;
  private Long you;
  private String reason;
  
  public Report(Long me, Long you, String reason) {
    this.me = me;
    this.you = you;
    this.reason = reason;
  }
  
  public Date getCreatedAt() {
    return createdAt;
  }
}