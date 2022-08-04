package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.dto.CommunityCommentReplyResponse;
import com.jocoos.mybeautip.domain.community.dto.CommunityMemberResponse;
import com.jocoos.mybeautip.domain.community.dto.ReportRequest;
import com.jocoos.mybeautip.domain.community.dto.WriteCommunityCommentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.DEFAULT_AVATAR_URL;

@Service
@RequiredArgsConstructor
public class CommunityCommentReplyService {

    public List<CommunityCommentReplyResponse> getReplies(long communityId, long commentId) {
        List<CommunityCommentReplyResponse> result = new ArrayList<>();

        CommunityMemberResponse memberResponse = CommunityMemberResponse.builder()
                .id(100L)
                .username("MockMember")
                .avatarUrl(DEFAULT_AVATAR_URL)
                .build();

        CommunityCommentReplyResponse item = CommunityCommentReplyResponse.builder()
                .id(1L)
                .contents("Mock Reply")
                .isLike(true)
                .likeCount(10)
                .createdAt(ZonedDateTime.now())
                .member(memberResponse)
                .build();

        result.add(item);

        return result;
    }

    public CommunityCommentReplyResponse getReply(long communityId, long commentId, long replyId) {

        CommunityMemberResponse memberResponse = CommunityMemberResponse.builder()
                .id(100L)
                .username("MockMember")
                .avatarUrl(DEFAULT_AVATAR_URL)
                .build();

        CommunityCommentReplyResponse result = CommunityCommentReplyResponse.builder()
                .id(1L)
                .contents("Mock Reply")
                .isLike(true)
                .likeCount(10)
                .createdAt(ZonedDateTime.now())
                .member(memberResponse)
                .build();

        return result;
    }

    public CommunityCommentReplyResponse write(WriteCommunityCommentRequest request) {
        CommunityMemberResponse memberResponse = CommunityMemberResponse.builder()
                .id(100L)
                .username("MockMember")
                .avatarUrl(DEFAULT_AVATAR_URL)
                .build();

        CommunityCommentReplyResponse result = CommunityCommentReplyResponse.builder()
                .id(1L)
                .contents("Mock Reply")
                .isLike(false)
                .likeCount(0)
                .createdAt(ZonedDateTime.now())
                .member(memberResponse)
                .build();

        return result;
    }

    public CommunityCommentReplyResponse edit(long replyId, WriteCommunityCommentRequest request) {
        CommunityMemberResponse memberResponse = CommunityMemberResponse.builder()
                .id(100L)
                .username("MockMember")
                .avatarUrl(DEFAULT_AVATAR_URL)
                .build();

        CommunityCommentReplyResponse result = CommunityCommentReplyResponse.builder()
                .id(1L)
                .contents("Mock Reply")
                .isLike(true)
                .likeCount(0)
                .createdAt(ZonedDateTime.now())
                .member(memberResponse)
                .build();

        return result;
    }

    public void delete(long communityId, long commentId, long replyId) {
        return;
    }

    public void like(long communityId, long commentId, long replyId, boolean isLike) {
        return;
    }

    public void report(long communityId, long commentId, long replyId, ReportRequest report) {
        return;
    }
}
