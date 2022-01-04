package com.jocoos.mybeautip.member.comment;

import javax.persistence.*;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.video.Video;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "comment_reports")
public class CommentReport extends MemberAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "comment_id")
  private Comment comment;

  @Column
  private Integer reasonCode;
  
  @Column(nullable = false)
  private String reason;

  public CommentReport(Comment comment, Member member, int reasonCode, String reason) {
    this.comment = comment;
    this.createdBy = member;
    this.reasonCode = reasonCode;
    this.reason = reason;
  }
}