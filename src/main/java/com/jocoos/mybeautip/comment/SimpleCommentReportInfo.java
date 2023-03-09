package com.jocoos.mybeautip.comment;

import org.springframework.beans.BeanUtils;

import java.util.Date;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.comment.CommentReport;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SimpleCommentReportInfo {

    private Long id;
    private Long commentId;
    private Integer reasonCode;
    private String reason;
    private SimpleMemberInfo createdBy;

    public SimpleCommentReportInfo(CommentReport commentReport) {
        BeanUtils.copyProperties(commentReport, this);
        commentId = commentReport.getComment().getId();
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
