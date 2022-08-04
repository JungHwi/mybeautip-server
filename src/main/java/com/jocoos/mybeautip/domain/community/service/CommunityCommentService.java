package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.dto.CommunityCommentResponse;
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
public class CommunityCommentService {

    public List<CommunityCommentResponse> getComments(long communityId) {
        List<CommunityCommentResponse> result = new ArrayList<>();

        CommunityMemberResponse memberResponse = CommunityMemberResponse.builder()
                .id(100L)
                .username("MockMember")
                .avatarUrl(DEFAULT_AVATAR_URL)
                .build();

        CommunityCommentResponse item = CommunityCommentResponse.builder()
                .id(1L)
                .contents("Mock Contents 1")
                .isLike(true)
                .member(memberResponse)
                .createdAt(ZonedDateTime.now())
                .likeCount(10)
                .commentCount(3)
                .build();

        result.add(item);

        return result;
    }

    public CommunityCommentResponse getComment(long communityId, long commentId) {
        CommunityMemberResponse memberResponse = CommunityMemberResponse.builder()
                .id(100L)
                .username("MockMember")
                .avatarUrl(DEFAULT_AVATAR_URL)
                .build();

        CommunityCommentResponse result = CommunityCommentResponse.builder()
                .id(1L)
                .contents("Mock Contents 1")
                .isLike(true)
                .member(memberResponse)
                .createdAt(ZonedDateTime.now())
                .likeCount(10)
                .commentCount(3)
                .build();

        return result;
    }

    public CommunityCommentResponse write(long communityId, WriteCommunityCommentRequest request) {
        CommunityMemberResponse memberResponse = CommunityMemberResponse.builder()
                .id(100L)
                .username("MockMember")
                .avatarUrl(DEFAULT_AVATAR_URL)
                .build();

        CommunityCommentResponse result = CommunityCommentResponse.builder()
                .id(1L)
                .contents("Mock Contents 1")
                .isLike(false)
                .likeCount(0)
                .commentCount(0)
                .member(memberResponse)
                .createdAt(ZonedDateTime.now())
                .build();

        return result;
    }

    public CommunityCommentResponse edit(long communityId, long commentId, WriteCommunityCommentRequest request) {
        CommunityMemberResponse memberResponse = CommunityMemberResponse.builder()
                .id(100L)
                .username("MockMember")
                .avatarUrl(DEFAULT_AVATAR_URL)
                .build();

        CommunityCommentResponse result = CommunityCommentResponse.builder()
                .id(1L)
                .contents("Mock Contents 1")
                .isLike(true)
                .likeCount(10)
                .commentCount(2)
                .createdAt(ZonedDateTime.now())
                .member(memberResponse)
                .build();

        return result;
    }

    public void delete(long communityId, long commentId) {
        return;
    }

    public void like(long communityId, long commentId, boolean isLike) {
        return;
    }

    public void report(long communityId, long commentId, ReportRequest report) {
        return;
    }
}
