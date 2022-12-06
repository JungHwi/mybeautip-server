package com.jocoos.mybeautip.domain.video.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jocoos.mybeautip.domain.community.dto.MemberResponse;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.comment.Comment;
import com.jocoos.mybeautip.member.comment.Comment.CommentState;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
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
    private final int likeCount;
    private final int reportCount;
    private MemberResponse member;
    private List<AdminVideoCommentResponse> children;

    @JsonFormat(pattern = ZONE_DATE_TIME_FORMAT)
    private final ZonedDateTime createdAt;


    @QueryProjection
    public AdminVideoCommentResponse(Comment comment, Member member, List<AdminVideoCommentResponse> children) {
        this(comment, member);
        this.children = children;
    }

    @QueryProjection
    public AdminVideoCommentResponse(Comment comment, Member member) {
        this.id = comment.getId();
        this.status = comment.getStateString();
        this.contents = comment.getComment();
        this.likeCount = comment.getLikeCount();
        this.reportCount = comment.getReportCount();
        this.createdAt = comment.getCreatedAtUTCZoned();
        this.member = MemberResponse.from(member);
    }
}