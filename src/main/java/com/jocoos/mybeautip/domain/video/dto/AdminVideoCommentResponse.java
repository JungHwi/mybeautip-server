package com.jocoos.mybeautip.domain.video.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.community.dto.AdminMemberResponse;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.Comment.CommentState;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.global.constant.LocalDateTimeConstant.ZONE_DATE_TIME_FORMAT;

@Getter
@RequiredArgsConstructor
public class AdminVideoCommentResponse {

    private final Long id;
    private final CommentState status;
    private final String contents;
    private final String fileUrl;
    private final int likeCount;
    private final int reportCount;
    private AdminMemberResponse member;
    private List<AdminVideoCommentResponse> children;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime createdAt;

    public AdminVideoCommentResponse(Comment comment) {
        this(comment, comment.getCreatedBy());
    }

    @QueryProjection
    public AdminVideoCommentResponse(Comment comment, Member member) {
        this.id = comment.getId();
        this.status = comment.getStateString();
        this.contents = comment.getComment();
        this.fileUrl = comment.getFileUrl();
        this.likeCount = comment.getLikeCount();
        this.reportCount = comment.getReportCount();
        this.createdAt = comment.getCreatedAtUTCZoned();
        this.member = AdminMemberResponse.from(member);
    }


    @QueryProjection
    public AdminVideoCommentResponse(Comment comment, Member member, List<AdminVideoCommentResponse> children) {
        this(comment, member);
        this.children = children;
    }
}
