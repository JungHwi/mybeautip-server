package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.dto.*;
import com.jocoos.mybeautip.domain.community.vo.CommunityRelationInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.DEFAULT_AVATAR_URL;

@Service
@RequiredArgsConstructor
public class CommunityCommentService {

    public List<CommunityCommentResponse> getComments(long communityId) {
        List<CommunityCommentResponse> result = new ArrayList<>();

        CommunityRelationInfo relationInfo = CommunityRelationInfo.builder()
                .isLike(true)
                .isBlock(false)
                .isReport(false)
                .build();

        CommunityMemberResponse memberResponse = CommunityMemberResponse.builder()
                .id(100L)
                .username("MockMember")
                .avatarUrl(DEFAULT_AVATAR_URL)
                .build();

        CommunityCommentResponse item = CommunityCommentResponse.builder()
                .id(1L)
                .contents("Mock Contents 1")
                .createdAt(ZonedDateTime.now())
                .likeCount(10)
                .replyCount(3)
                .reportCount(0)
                .relationInfo(relationInfo)
                .member(memberResponse)
                .build();

        result.add(item);

        return result;
    }

    public CommunityCommentResponse getComment(long communityId, long commentId) {
        CommunityRelationInfo relationInfo = CommunityRelationInfo.builder()
                .isLike(true)
                .isBlock(false)
                .isReport(false)
                .build();

        CommunityMemberResponse memberResponse = CommunityMemberResponse.builder()
                .id(100L)
                .username("MockMember")
                .avatarUrl(DEFAULT_AVATAR_URL)
                .build();

        CommunityCommentResponse result = CommunityCommentResponse.builder()
                .id(1L)
                .contents("Mock Contents 1")
                .likeCount(10)
                .replyCount(3)
                .reportCount(0)
                .createdAt(ZonedDateTime.now())
                .relationInfo(relationInfo)
                .member(memberResponse)
                .build();

        return result;
    }

    public CommunityCommentResponse write(long communityId, WriteCommunityCommentRequest request) {
        CommunityRelationInfo relationInfo = CommunityRelationInfo.builder()
                .isLike(true)
                .isBlock(false)
                .isReport(false)
                .build();

        CommunityMemberResponse memberResponse = CommunityMemberResponse.builder()
                .id(100L)
                .username("MockMember")
                .avatarUrl(DEFAULT_AVATAR_URL)
                .build();

        CommunityCommentResponse result = CommunityCommentResponse.builder()
                .id(1L)
                .contents("Mock Contents 1")
                .likeCount(0)
                .replyCount(0)
                .reportCount(0)
                .createdAt(ZonedDateTime.now())
                .relationInfo(relationInfo)
                .member(memberResponse)
                .build();

        return result;
    }

    public CommunityCommentResponse edit(long communityId, long commentId, WriteCommunityCommentRequest request) {
        CommunityRelationInfo relationInfo = CommunityRelationInfo.builder()
                .isLike(true)
                .isBlock(false)
                .isReport(false)
                .build();

        CommunityMemberResponse memberResponse = CommunityMemberResponse.builder()
                .id(100L)
                .username("MockMember")
                .avatarUrl(DEFAULT_AVATAR_URL)
                .build();

        CommunityCommentResponse result = CommunityCommentResponse.builder()
                .id(1L)
                .contents("Mock Contents 1")
                .likeCount(10)
                .replyCount(2)
                .reportCount(0)
                .createdAt(ZonedDateTime.now())
                .relationInfo(relationInfo)
                .member(memberResponse)
                .build();

        return result;
    }

    public void delete(long communityId, long commentId) {
        return;
    }

    public LikeResponse like(long communityId, long commentId, boolean isLike) {
        return LikeResponse.builder()
                .isLike(isLike)
                .likeCount(10)
                .build();
    }

    public void report(long communityId, long commentId, ReportRequest report) {
        return;
    }
}
