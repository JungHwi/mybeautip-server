package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.DEFAULT_AVATAR_URL;
import static com.jocoos.mybeautip.global.constant.MybeautipConstant.DELETED_AVATAR_URL;

@Service
@RequiredArgsConstructor
public class CommunityService {

    public List<CommunityResponse> getCommunities() {
        List<CommunityResponse> communityResponseList = new ArrayList<>();

        CommunityMemberResponse memberResponse = CommunityMemberResponse.builder()
                .id(100L)
                .username("MockMember")
                .avatarUrl(DEFAULT_AVATAR_URL)
                .build();

        CommunityCategoryResponse normalCategory = CommunityCategoryResponse.builder()
                .id(1L)
                .title("Mock Normal")
                .type(CommunityCategoryType.NORMAL)
                .build();

        CommunityCategoryResponse blindCategory = CommunityCategoryResponse.builder()
                .id(2L)
                .title("Mock Blind")
                .type(CommunityCategoryType.BLIND)
                .build();

        CommunityCategoryResponse dripCategory = CommunityCategoryResponse.builder()
                .id(3L)
                .title("Mock Drip")
                .type(CommunityCategoryType.DRIP)
                .build();

        CommunityResponse normalCommunity = CommunityResponse.builder()
                .id(3L)
                .contents("Mock Normal Contents 1")
                .fileUrl(Arrays.asList(DEFAULT_AVATAR_URL, DELETED_AVATAR_URL))
                .isLike(false)
                .member(memberResponse)
                .category(normalCategory)
                .createdAt(ZonedDateTime.now())
                .likeCount(50)
                .commentCount(5)
                .build();

        communityResponseList.add(normalCommunity);

        CommunityResponse blindCommunity = CommunityResponse.builder()
                .id(2L)
                .title("Mock Blind Title")
                .isLike(false)
                .category(blindCategory)
                .createdAt(ZonedDateTime.now().minusMinutes(10))
                .likeCount(10)
                .commentCount(2)
                .build();

        communityResponseList.add(blindCommunity);

        CommunityResponse dripCommunity = CommunityResponse.builder()
                .id(1L)
                .category(dripCategory)
                .isLike(false)
                .contents("Mock Drip Contents")
                .createdAt(ZonedDateTime.now().minusMinutes(100))
                .likeCount(20)
                .commentCount(3)
                .member(memberResponse)
                .build();

        communityResponseList.add(dripCommunity);

        return communityResponseList;
    }

    public CommunityResponse get(Long communityId) {
        CommunityMemberResponse memberResponse = CommunityMemberResponse.builder()
                .id(100L)
                .username("MockMember")
                .avatarUrl(DEFAULT_AVATAR_URL)
                .build();

        CommunityCategoryResponse normalCategory = CommunityCategoryResponse.builder()
                .id(1L)
                .title("Mock Normal")
                .type(CommunityCategoryType.NORMAL)
                .build();

        CommunityResponse result = CommunityResponse.builder()
                .id(1L)
                .contents("Mock Contents")
                .isLike(false)
                .fileUrl(Arrays.asList(DEFAULT_AVATAR_URL, DELETED_AVATAR_URL))
                .viewCount(1234)
                .likeCount(20)
                .commentCount(3)
                .createdAt(ZonedDateTime.now())
                .member(memberResponse)
                .category(normalCategory)
                .build();

        return result;
    }

    public CommunityResponse write(WriteCommunityRequest request) {

        CommunityMemberResponse memberResponse = CommunityMemberResponse.builder()
                .id(100L)
                .username("MockMember")
                .avatarUrl(DEFAULT_AVATAR_URL)
                .build();

        CommunityCategoryResponse normalCategory = CommunityCategoryResponse.builder()
                .id(1L)
                .title("Mock Normal")
                .type(CommunityCategoryType.NORMAL)
                .build();

        return CommunityResponse.builder()
                .id(1L)
                .title("Mock Title")
                .contents("Mock Contents")
                .fileUrl(Arrays.asList(DEFAULT_AVATAR_URL, DELETED_AVATAR_URL))
                .isLike(false)
                .viewCount(123)
                .likeCount(12)
                .commentCount(1)
                .createdAt(ZonedDateTime.now())
                .member(memberResponse)
                .category(normalCategory)
                .build();
    }

    public List<String> upload(List<MultipartFile> files) {
        return Arrays.asList(DEFAULT_AVATAR_URL, DELETED_AVATAR_URL);
    }

    public CommunityResponse edit(EditCommunityRequest request) {
        CommunityMemberResponse memberResponse = CommunityMemberResponse.builder()
                .id(100L)
                .username("MockMember")
                .avatarUrl(DEFAULT_AVATAR_URL)
                .build();

        CommunityCategoryResponse normalCategory = CommunityCategoryResponse.builder()
                .id(1L)
                .title("Mock Normal")
                .type(CommunityCategoryType.NORMAL)
                .build();

        return CommunityResponse.builder()
                .id(1L)
                .title("Mock Title")
                .contents("Mock Contents")
                .fileUrl(Arrays.asList(DEFAULT_AVATAR_URL, DELETED_AVATAR_URL))
                .isLike(false)
                .viewCount(123)
                .likeCount(12)
                .commentCount(1)
                .createdAt(ZonedDateTime.now())
                .member(memberResponse)
                .category(normalCategory)
                .build();
    }

    public void delete(Long communityId) {
        return;
    }

    public void like(long memberId, long communityId, boolean isLike) {
        return;
    }

    public void report(long memberId, long communityId, ReportRequest report) {
        return;
    }
}
