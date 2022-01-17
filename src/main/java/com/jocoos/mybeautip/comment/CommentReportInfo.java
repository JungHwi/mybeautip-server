package com.jocoos.mybeautip.comment;

import org.springframework.beans.BeanUtils;

import java.util.Date;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.comment.CommentInfo;
import com.jocoos.mybeautip.member.comment.CommentReport;
import com.jocoos.mybeautip.restapi.VideoController;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommentReportInfo {

  private Long id;
  private CommentInfo comment;
  private Integer reasonCode;
  private String reason;
  private SimpleMemberInfo createdBy;

  public CommentReportInfo(CommentReport commentReport) {
    BeanUtils.copyProperties(commentReport, this);
    comment = new CommentInfo(commentReport.getComment());
    createdBy = new SimpleMemberInfo(commentReport.getCreatedBy());
  }

  @Data
  private static class SimpleMemberInfo {
    private Long id;
    private String username;
    private Date createdAt;

    public SimpleMemberInfo(Member member) {
      BeanUtils.copyProperties(member, this);
    }
  }
}
