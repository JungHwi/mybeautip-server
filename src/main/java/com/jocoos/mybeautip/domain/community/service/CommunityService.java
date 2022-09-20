package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.community.converter.CommunityConverter;
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
import com.jocoos.mybeautip.domain.member.dto.MyCommunityResponse;
import com.jocoos.mybeautip.domain.point.service.ActivityPointService;
import com.jocoos.mybeautip.global.code.UrlDirectory;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.exception.AccessDeniedException;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.jocoos.mybeautip.domain.community.code.CommunityCategoryType.DRIP;
import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.GET_LIKE_COMMUNITY;
import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.WRITE_COMMUNITY_TYPES;
import static com.jocoos.mybeautip.domain.point.service.activity.ValidObject.validDomainAndReceiver;


@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRelationService relationService;
    private final EventJoinService eventJoinService;

    private final ActivityPointService activityPointService;
    private final LegacyMemberService legacyMemberService;

    private final CommunityCategoryDao categoryDao;
    private final CommunityDao communityDao;
    private final CommunityLikeDao likeDao;
    private final CommunityReportDao reportDao;

    private final CommunityConverter converter;
    private final AwsS3Handler awsS3Handler;

    @Transactional
    public CommunityResponse write(WriteCommunityRequest request) {
        request.setMember(legacyMemberService.currentMember());
        Community community = communityDao.write(request);

        awsS3Handler.copy(request.getFiles(), UrlDirectory.COMMUNITY.getDirectory(community.getId()));

        if (community.getCategory().getType() == DRIP) {
            eventJoinService.join(community.getEventId(), request.getMember().getId());
        }

        activityPointService.gainActivityPoint(WRITE_COMMUNITY_TYPES,
                validDomainAndReceiver(community, community.getId(), community.getMember()));

        return getCommunity(community.getMember(), community);
    }

    @Transactional()
    public CommunityResponse getCommunity(long communityId) {
        Community community = communityDao.get(communityId);

        Member member = legacyMemberService.currentMember();

        communityDao.readCount(communityId);

        return getCommunity(member, community);
    }

    private CommunityResponse getCommunity(Member member, Community community) {
        CommunityResponse response = converter.convert(community);
        return relationService.setRelationInfo(member, response);
    }

    @Transactional(readOnly = true)
    public List<CommunityResponse> getCommunities(SearchCommunityRequest request, Pageable pageable) {
        CommunitySearchCondition condition = createSearchCondition(request);
        List<Community> communities = communityDao.getCommunities(condition, pageable);
        return getCommunity(legacyMemberService.currentMember(), communities);
    }

    @Transactional(readOnly = true)
    public List<MyCommunityResponse> getMyCommunities(long cursor, Pageable pageable) {
        Member member = legacyMemberService.currentMember();

        List<Community> communityList = communityDao.get(member.getId(), cursor, pageable);
        List<MyCommunityResponse> communityResponses = converter.convertToMyCommunity(communityList);

        for (MyCommunityResponse response : communityResponses) {
            response.changeContents();
        }

        return communityResponses;
    }

    private List<CommunityResponse> getCommunity(Member member, List<Community> communities) {
        List<CommunityResponse> responses = converter.convert(communities);
        return relationService.setRelationInfo(member, responses);
    }

    public List<String> upload(List<MultipartFile> files) {
        return awsS3Handler.upload(files, UrlDirectory.TEMP.getDirectory());
    }

    @Transactional
    public void delete(long communityId) {
        Member member = legacyMemberService.currentMember();
        Community community = communityDao.get(communityId);

        if (!community.getMember().getId().equals(member.getId())) {
            throw new AccessDeniedException(ErrorCode.ACCESS_DENIED, "This is not yours.");
        }

        community.delete();
        activityPointService.retrieveActivityPoint(WRITE_COMMUNITY_TYPES,
                validDomainAndReceiver(community, community.getId(), member));
    }

    @Transactional
    public CommunityResponse edit(EditCommunityRequest request) {
        Member member = legacyMemberService.currentMember();
        Community community = communityDao.get(request.getCommunityId());

        if (!community.getMember().getId().equals(member.getId())) {
            throw new AccessDeniedException(ErrorCode.ACCESS_DENIED, "This is not yours.");
        }

        community.setTitle(request.getTitle());
        community.setContents(request.getContents());
        editFiles(community, request.getFiles());

        communityDao.save(community);

        return getCommunity(community.getMember(), community);
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
        CommunityReport report = reportDao.report(memberId, communityId, reportRequest);

        Community community = communityDao.get(communityId);
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

    @Transactional
    public void editFiles(Community community, List<FileDto> fileDtoList) {
        if (CollectionUtils.isEmpty(fileDtoList) || community.isVoteAndIncludeFile()) {
            return;
        }

        for (FileDto fileDto : fileDtoList) {
            switch (fileDto.getOperation()) {
                case UPLOAD:
                    community.addFile(fileDto.getUrl());
                    break;
                case DELETE:
                    community.removeFile(fileDto.getUrl());
                    break;
            }
        }

        awsS3Handler.editFiles(fileDtoList, UrlDirectory.COMMUNITY.getDirectory(community.getId()));
    }

    private CommunitySearchCondition createSearchCondition(SearchCommunityRequest request) {
        List<CommunityCategory> categories = categoryDao.getCategoryForSearchCommunity(request.getCategoryId());
        return new CommunitySearchCondition(request.getEventId(), request.getCursor(), categories);
    }
}
