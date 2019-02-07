package com.jocoos.mybeautip.member.report;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.video.Video;

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
  
  private Integer reasonCode;
  
  @Column(nullable = false)
  private String reason;
  
  @ManyToOne
  @JoinColumn(name = "video_id")
  private Video video;
  
  public Report(Member me, Member you, int reasonCode, String reason, Video video) {
    this.me = me;
    this.you = you;
    this.reasonCode = reasonCode;
    this.reason = reason;
    this.video = video;
  }

  public Member getReporter() {
    return me;
  }

  public Member getReportee() {
    return you;
  }
}