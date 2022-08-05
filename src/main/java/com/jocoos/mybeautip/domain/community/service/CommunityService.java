package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.dto.*;
import com.jocoos.mybeautip.domain.community.vo.CommunityRelationInfo;
import com.jocoos.mybeautip.global.code.UrlDirectory;
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

    private final AwsS3Handler awsS3Handler;

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
                .hint("Mock Normal Hint")
                .build();

        CommunityCategoryResponse blindCategory = CommunityCategoryResponse.builder()
                .id(2L)
                .title("Mock Blind")
                .type(CommunityCategoryType.BLIND)
                .hint("Mock Blind Hint")
                .build();

        CommunityCategoryResponse dripCategory = CommunityCategoryResponse.builder()
                .id(3L)
                .title("Mock Drip")
                .type(CommunityCategoryType.DRIP)
                .hint("Mock DRIP Hint")
                .build();

        CommunityRelationInfo relationInfo = CommunityRelationInfo.builder()
                .isLike(true)
                .isBlock(false)
                .isReport(false)
                .build();

        CommunityResponse normalCommunity = CommunityResponse.builder()
                .id(3L)
                .contents("Mock Normal Contents 1")
                .fileUrl(Arrays.asList(DEFAULT_AVATAR_URL, DELETED_AVATAR_URL))
                .createdAt(ZonedDateTime.now())
                .viewCount(1000)
                .likeCount(50)
                .commentCount(5)
                .reportCount(5)
                .sortedAt(ZonedDateTime.now())
                .relationInfo(relationInfo)
                .member(memberResponse)
                .category(normalCategory)
                .build();

        communityResponseList.add(normalCommunity);

        CommunityResponse blindCommunity = CommunityResponse.builder()
                .id(2L)
                .title("Mock Blind Title")
                .createdAt(ZonedDateTime.now().minusMinutes(10))
                .viewCount(1000)
                .likeCount(10)
                .commentCount(2)
                .reportCount(0)
                .sortedAt(ZonedDateTime.now())
                .relationInfo(relationInfo)
                .category(normalCategory)
                .build();

        communityResponseList.add(blindCommunity);

        CommunityResponse dripCommunity = CommunityResponse.builder()
                .id(1L)
                .contents("Mock Drip Contents")
                .createdAt(ZonedDateTime.now().minusMinutes(100))
                .viewCount(1000)
                .likeCount(20)
                .commentCount(3)
                .reportCount(0)
                .sortedAt(ZonedDateTime.now())
                .relationInfo(relationInfo)
                .member(memberResponse)
                .category(normalCategory)
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
                .hint("Mock Normal Hint")
                .build();

        CommunityRelationInfo relationInfo = CommunityRelationInfo.builder()
                .isLike(true)
                .isBlock(false)
                .isReport(false)
                .build();

        CommunityResponse result = CommunityResponse.builder()
                .id(1L)
                .contents("Mock Contents")
                .fileUrl(Arrays.asList(DEFAULT_AVATAR_URL, DELETED_AVATAR_URL))
                .viewCount(1234)
                .likeCount(20)
                .commentCount(3)
                .reportCount(0)
                .createdAt(ZonedDateTime.now())
                .relationInfo(relationInfo)
                .member(memberResponse)
                .category(normalCategory)
                .build();

        return result;
    }

    public CommunityResponse write(WriteCommunityRequest request) {

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

        CommunityCategoryResponse normalCategory = CommunityCategoryResponse.builder()
                .id(1L)
                .title("Mock Normal")
                .type(CommunityCategoryType.NORMAL)
                .hint("Mock Normal Hint")
                .build();

        return CommunityResponse.builder()
                .id(1L)
                .title("Mock Title")
                .contents("Mock Contents")
                .fileUrl(Arrays.asList(DEFAULT_AVATAR_URL, DELETED_AVATAR_URL))
                .viewCount(123)
                .likeCount(12)
                .commentCount(1)
                .createdAt(ZonedDateTime.now())
                .relationInfo(relationInfo)
                .member(memberResponse)
                .category(normalCategory)
                .build();
    }

    public List<String> upload(List<MultipartFile> files) {
        return awsS3Handler.upload(files, UrlDirectory.TEMP.getDirectory());
    }

    public CommunityResponse edit(EditCommunityRequest request) {
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

        CommunityCategoryResponse normalCategory = CommunityCategoryResponse.builder()
                .id(1L)
                .title("Mock Normal")
                .type(CommunityCategoryType.NORMAL)
                .hint("Mock Normal Hint")
                .build();

        return CommunityResponse.builder()
                .id(1L)
                .title("Mock Title")
                .contents("Mock Contents")
                .fileUrl(Arrays.asList(DEFAULT_AVATAR_URL, DELETED_AVATAR_URL))
                .viewCount(123)
                .likeCount(12)
                .commentCount(1)
                .reportCount(0)
                .createdAt(ZonedDateTime.now())
                .relationInfo(relationInfo)
                .member(memberResponse)
                .category(normalCategory)
                .build();
    }

    public void delete(Long communityId) {
        return;
    }

    public LikeResponse like(long memberId, long communityId, boolean isLike) {
        return LikeResponse.builder()
                .isLike(isLike)
                .likeCount(10)
                .build();
    }

    public void report(long memberId, long communityId, ReportRequest report) {
        return;
    }
}
