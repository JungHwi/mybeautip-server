package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.dto.*;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityLike;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityReport;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCategoryDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityLikeDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityReportDao;
import com.jocoos.mybeautip.domain.community.vo.CommunitySearchCondition;
import com.jocoos.mybeautip.domain.event.service.EventJoinService;
import com.jocoos.mybeautip.domain.member.code.Role;
import com.jocoos.mybeautip.domain.member.dto.MyCommunityResponse;
import com.jocoos.mybeautip.domain.member.service.dao.MemberActivityCountDao;
import com.jocoos.mybeautip.domain.point.service.ActivityPointService;
import com.jocoos.mybeautip.global.exception.AccessDeniedException;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.DRIP;
import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.GET_LIKE_COMMUNITY;
import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.WRITE_COMMUNITY_TYPES;
import static com.jocoos.mybeautip.domain.point.service.activity.ValidObject.validDomainAndReceiver;
import static com.jocoos.mybeautip.global.exception.ErrorCode.ACCESS_DENIED;


@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityService {

    private final EventJoinService eventJoinService;
    private final ActivityPointService activityPointService;
    private final LegacyMemberService legacyMemberService;
    private final CommunityCategoryDao categoryDao;
    private final CommunityDao communityDao;
    private final CommunityLikeDao likeDao;
    private final CommunityReportDao reportDao;
    private final MemberActivityCountDao activityCountDao;
    private final CommunityConvertService convertService;
    private final CommunityCommentDeleteService commentDeleteService;
    private final CommunityFileService fileService;

    @Transactional
    public CommunityResponse write(WriteCommunityRequest request) {
        Member member = legacyMemberService.currentMember();
        request.setMember(member);
        Community community = communityDao.write(request);

        if (community.getCategory().getType() == DRIP) {
            eventJoinService.join(community.getEventId(), request.getMember().getId());
        }

        activityPointService.gainActivityPoint(WRITE_COMMUNITY_TYPES,
                validDomainAndReceiver(community, community.getId(), community.getMember()));
        activityCountDao.updateAllCommunityCount(member, 1);

        uploadFiles(request, community);
        return convertService.toResponse(community.getMember(), community);
    }

    @Transactional()
    public CommunityResponse getCommunity(long communityId) {
        communityDao.readCount(communityId);

        Community community = communityDao.get(communityId);
        Member member = legacyMemberService.currentMember();
        community.validReadAuth(Role.from(member));

        return convertService.toResponse(member, community);
    }

    @Transactional(readOnly = true)
    public List<CommunityResponse> getCommunities(SearchCommunityRequest request, Pageable pageable) {
        CommunitySearchCondition condition = createSearchCondition(request);
        List<Community> communities = communityDao.getCommunities(condition, pageable);
        return convertService.toResponse(legacyMemberService.currentMember(), communities);
    }

    @Transactional(readOnly = true)
    public List<MyCommunityResponse> getMyCommunities(long cursor, Pageable pageable) {
        Member member = legacyMemberService.currentMember();

        List<Community> communityList = communityDao.get(member.getId(), cursor, pageable);
        List<MyCommunityResponse> communityResponses = convertService.toMyCommunityResponse(communityList);

        for (MyCommunityResponse response : communityResponses) {
            response.changeContents();
        }

        return communityResponses;
    }

    @Transactional
    public void delete(long communityId) {
        Member member = legacyMemberService.currentMember();
        Community community = communityDao.get(communityId);

        if (!community.getMember().getId().equals(member.getId())) {
            throw new AccessDeniedException(ACCESS_DENIED, "This is not yours.");
        }

        community.delete();
        commentDeleteService.delete(community.getId());

        activityPointService.retrieveActivityPoint(WRITE_COMMUNITY_TYPES,
                validDomainAndReceiver(community, community.getId(), member));
        activityCountDao.updateNormalCommunityCount(member, -1);
    }

    @Transactional
    public CommunityResponse edit(EditCommunityRequest request) {
        Member member = legacyMemberService.currentMember();
        Community community = communityDao.get(request.getCommunityId());

        if (!community.getMember().getId().equals(member.getId())) {
            throw new AccessDeniedException(ACCESS_DENIED, "This is not yours.");
        }

        community.setTitle(request.getTitle());
        community.setContents(request.getContents());

        editFiles(request, community);

        return convertService.toResponse(community.getMember(), community);
    }

    @Transactional
    public LikeResponse like(long memberId, long communityId, boolean isLike) {
        CommunityLike like = likeDao.like(memberId, communityId, isLike);

        Community community = communityDao.get(communityId);

        gainActivityPoint(isLike, like, community.getMember());

        return LikeResponse.builder()
                .isLike(isLike)
                .likeCount(community.getLikeCount())
                .build();
    }

    private void gainActivityPoint(boolean isLike, CommunityLike like, Member member) {
        if (isLike) {
            activityPointService.gainActivityPoint(GET_LIKE_COMMUNITY, validDomainAndReceiver(like, like.getId(), member));
        }
    }

    @Transactional
    public ReportResponse report(long memberId, long communityId, ReportRequest reportRequest) {

        Community community = communityDao.get(communityId);
        CommunityReport report = reportDao.report(memberId, community.getMemberId(), communityId, reportRequest);

        if (community.getMemberId().equals(memberId)) {
            throw new BadRequestException("this is my community.");
        }

        return ReportResponse.builder()
                .isReport(report.isReport())
                .reportCount(community.getReportCount())
                .build();
    }

    @Transactional(readOnly = true)
    public ReportResponse isReport(long memberId, long communityId) {
        boolean isReport = reportDao.isReport(memberId, communityId);

        Community community = communityDao.get(communityId);

        return ReportResponse.builder()
                .isReport(isReport)
                .reportCount(community.getReportCount())
                .build();
    }

    private void uploadFiles(WriteCommunityRequest request, Community community) {
        if (request.containTranscodeRequest()) {
            fileService.writeWithTranscode(request.getFiles(), community.getId());
        } else {
            fileService.write(request.getFiles(), community.getId());
        }
    }

    private void editFiles(EditCommunityRequest request, Community community) {
        if (request.containTranscodeRequest()) {
            fileService.editFilesWithTranscode(community, request.getFiles());
        } else {
            fileService.editFiles(community, request.getFiles());
        }
    }

    private CommunitySearchCondition createSearchCondition(SearchCommunityRequest request) {
        List<CommunityCategory> categories = categoryDao.getCategoryForSearchCommunity(request.getCategoryId());
        Long memberId = legacyMemberService.currentMemberId();
        return CommunitySearchCondition.builder()
                .eventId(request.getEventId())
                .cursor(request.getCursor())
                .categories(categories)
                .memberId(memberId)
                .build();
    }
}
