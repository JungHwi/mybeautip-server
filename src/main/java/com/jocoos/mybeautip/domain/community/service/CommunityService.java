package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.converter.CommunityConverter;
import com.jocoos.mybeautip.domain.community.dto.*;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityReport;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCategoryDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityLikeDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityReportDao;
import com.jocoos.mybeautip.domain.event.service.EventJoinService;
import com.jocoos.mybeautip.domain.member.dto.MyCommunityResponse;
import com.jocoos.mybeautip.global.code.UrlDirectory;
import com.jocoos.mybeautip.global.dto.FileDto;
import com.jocoos.mybeautip.global.exception.AccessDeniedException;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRelationService relationService;
    private final EventJoinService eventJoinService;
    private final LegacyMemberService legacyMemberService;

    private final CommunityCategoryDao categoryDao;
    private final CommunityDao communityDao;
    private final CommunityLikeDao likeDao;
    private final CommunityReportDao reportDao;

    private final CommunityConverter converter;
    private final AwsS3Handler awsS3Handler;

    @Transactional
    public CommunityResponse write(WriteCommunityRequest request) {
        Community community = communityDao.write(request);

        awsS3Handler.copy(request.getFiles(), UrlDirectory.COMMUNITY.getDirectory(community.getId()));

        if (community.getCategory().getType() == CommunityCategoryType.DRIP) {
            eventJoinService.join(community.getEventId(), request.getMember().getId());
        }

        return getCommunity(community.getMember(), community);
    }

    @Transactional()
    public CommunityResponse getCommunity(Member member, long communityId) {
        Community community = communityDao.get(communityId);
        communityDao.readCount(communityId);

        return getCommunity(member, community);
    }

    private CommunityResponse getCommunity(Member member, Community community) {
        CommunityResponse response = converter.convert(community);
        return relationService.setRelationInfo(member, response);
    }

    @Transactional(readOnly = true)
    public List<CommunityResponse> getCommunities(SearchCommunityRequest request, Pageable pageable) {
        Member member = legacyMemberService.currentMember();
        request.setMember(member);

        // FIXME Dynamic Query to QueryDSL
        List<CommunityCategory> categories = categoryDao.getCategoryForSearchCommunity(request.getCategoryId());
        List<Community> communityList = new ArrayList<>();
        List<Community> winList = new ArrayList<>();
        List<Community> loseList = new ArrayList<>();

        if (categories.size() == 1) {
            CommunityCategory category = categories.get(0);
            if (category.getType() == CommunityCategoryType.DRIP) {
                if (request.getEventId() == null || request.getEventId() < NumberUtils.LONG_ONE) {
                    throw new BadRequestException("need_event_info", "event_id is required to search DRIP category.");
                }
                if (request.isFirstSearch()) {
                    winList = communityDao.getCommunityForEvent(request.getEventId(), categories, true, request.getCursor(), pageable);
                }
                loseList = communityDao.getCommunityForEvent(request.getEventId(), categories, null, request.getCursor(), pageable);
                communityList = Stream.concat(winList.stream(), loseList.stream())
                        .collect(Collectors.toList());
            } else {
                communityList = communityDao.get(categories, request.getCursor(), pageable);
            }
        } else {
            communityList = communityDao.get(categories, request.getCursor(), pageable);
        }

        return getCommunity(request.getMember(), communityList);
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
    public void delete(Member member, long communityId) {
        Community community = communityDao.get(communityId);

        if (!community.getMember().getId().equals(member.getId())) {
            throw new AccessDeniedException("access_denied", "This is not yours.");
        }

        community.delete();
    }

    @Transactional
    public CommunityResponse edit(EditCommunityRequest request) {
        Community community = communityDao.get(request.getCommunityId());

        if (!community.getMember().getId().equals(request.getMember().getId())) {
            throw new AccessDeniedException("access_denied", "This is not yours.");
        }

        community.setTitle(request.getTitle());
        community.setContents(request.getContents());
        editFiles(community, request.getFiles());

        return getCommunity(community.getMember(), community);
    }

    @Transactional
    public LikeResponse like(long memberId, long communityId, boolean isLike) {
        likeDao.like(memberId, communityId, isLike);

        Community community = communityDao.get(communityId);

        return LikeResponse.builder()
                .isLike(isLike)
                .likeCount(community.getLikeCount())
                .build();
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
        if (CollectionUtils.isEmpty(fileDtoList)) {
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
}
