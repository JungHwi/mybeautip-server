package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.converter.CommunityConverter;
import com.jocoos.mybeautip.domain.community.dto.*;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCategoryDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.community.vo.CommunityRelationInfo;
import com.jocoos.mybeautip.domain.event.service.EventJoinService;
import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.global.code.UrlDirectory;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.DEFAULT_AVATAR_URL;
import static com.jocoos.mybeautip.global.constant.MybeautipConstant.DELETED_AVATAR_URL;


@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRelationService relationService;
    private final EventJoinService eventJoinService;

    private final CommunityCategoryDao categoryDao;
    private final CommunityDao communityDao;

    private final CommunityConverter converter;
    private final AwsS3Handler awsS3Handler;

    @Transactional
    public CommunityResponse write(WriteCommunityRequest request) {
        Community community = communityDao.write(request);

        awsS3Handler.copy(request.getFiles(), UrlDirectory.COMMUNITY.getDirectory(community.getId()));

        if (community.getEventId() != null) {
            eventJoinService.join(community.getEventId(), request.getMember().getId());
        }

        return getCommunity(community.getMember(), community);
    }

    @Transactional(readOnly = true)
    public CommunityResponse getCommunity(Member member, long communityId) {
        Community community = communityDao.get(communityId);
        return getCommunity(member, community);
    }

    private CommunityResponse getCommunity(Member member, Community community) {
        CommunityResponse response = converter.convert(community);
        return relationService.setRelationInfo(member, response);
    }

    public List<CommunityResponse> getCommunities(SearchCommunityRequest request, Pageable pageable) {
        // FIXME Dynamic Query to QueryDSL
        List<CommunityCategory> categories = categoryDao.getCategoryForSearchCommunity(request.getCategoryId());
        List<Community> communityList;

        if (categories.size() == 1) {
            CommunityCategory category = categories.get(0);
            if (category.getType() == CommunityCategoryType.DRIP) {
                if (request.getEventId() == null || request.getEventId() < 1) {
                    throw new BadRequestException("need_event_info", "event_id is required to search DRIP category.");
                }
                communityList = communityDao.getCommunityForEvent(request.getEventId(), categories, request.getCursor(), pageable);
            } else {
                communityList = communityDao.get(categories, request.getCursor(), pageable);
            }
        } else {
            communityList = communityDao.get(categories, request.getCursor(), pageable);
        }



        return getCommunity(request.getMember(), communityList);

    }

    private List<CommunityResponse> getCommunity(Member member, List<Community> communities) {
        List<CommunityResponse> responses = converter.convert(communities);

        return relationService.setRelationInfo(member, responses);
    }

    public List<String> upload(List<MultipartFile> files) {
        return awsS3Handler.upload(files, UrlDirectory.TEMP.getDirectory());
    }

    @Transactional
    public CommunityResponse edit(EditCommunityRequest request) {
//        Community community = communityDao.get(request.getCommunityId());
//
//        if (!community.getMember().equals(community.getMember())) {
//            throw new AccessDeniedException("dont_have_access", "This is not yours.");
//        }

        CommunityRelationInfo relationInfo = CommunityRelationInfo.builder()
                .isLike(true)
                .isBlock(false)
                .isReport(false)
                .build();

        CommunityMemberResponse memberResponse = new CommunityMemberResponse(100L, MemberStatus.ACTIVE, "MockMember", DEFAULT_AVATAR_URL);

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
