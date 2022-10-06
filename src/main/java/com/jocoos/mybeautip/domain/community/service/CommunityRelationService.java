package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.dto.CommunityMemberResponse;
import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.community.dto.CommunityScrapResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityLike;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityReport;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityLikeDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityReportDao;
import com.jocoos.mybeautip.domain.community.vo.CommunityRelationInfo;
import com.jocoos.mybeautip.domain.member.dao.MemberBlockDao;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.block.Block;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityRelationService {
    private final LegacyMemberService memberService;
    private final CommunityLikeDao likeDao;
    private final CommunityReportDao reportDao;
    private final MemberBlockDao blockDao;

    @Transactional(readOnly = true)
    public CommunityResponse setRelationInfo(CommunityResponse communityResponse) {
        Member member = memberService.currentMember();
        if (member == null) {
            return communityResponse.setRelationInfo(new CommunityRelationInfo());
        }

        CommunityRelationInfo relationInfo = CommunityRelationInfo.builder()
                .isLike(likeDao.isLike(member.getId(), communityResponse.getId()))
                .isReport(reportDao.isReport(member.getId(), communityResponse.getId()))
                .isBlock(communityResponse.getCategory().getType() != CommunityCategoryType.BLIND && blockDao.isBlock(member.getId(), communityResponse.getMember().getId()))
                .build();

        return communityResponse.setRelationInfo(member, relationInfo);
    }

    @Transactional(readOnly = true)
    public List<CommunityResponse> setRelationInfo(List<CommunityResponse> communityResponseList) {
        Member member = memberService.currentMember();
        CommunityRelationInfo relationInfo = new CommunityRelationInfo();
        if (member == null) {
            for (CommunityResponse communityResponse : communityResponseList) {
                communityResponse.setRelationInfo(relationInfo);
            }
            return communityResponseList;
        }

        List<Long> communityIds = communityResponseList.stream()
                .map(CommunityResponse::getId)
                .collect(Collectors.toList());

        List<Long> writerIds = communityResponseList.stream()
                .map(CommunityResponse::getMember)
                .collect(Collectors.toList()).stream()
                .map(CommunityMemberResponse::getId)
                .collect(Collectors.toList());

        Map<Long, CommunityLike> likeMap = getLikeMap(member.getId(), communityIds);
        Map<Long, CommunityReport> reportMap = getReportMap(member.getId(), communityIds);
        Map<Long, Block> blockMap = getBlockMap(member.getId(), writerIds);

        for (CommunityResponse communityResponse : communityResponseList) {
            relationInfo = CommunityRelationInfo.builder()
                    .isLike(likeMap.containsKey(communityResponse.getId()))
                    .isReport(reportMap.containsKey(communityResponse.getId()))
                    .isBlock(communityResponse.getCategory().getType() != CommunityCategoryType.BLIND && blockMap.containsKey(communityResponse.getMember().getId()))
                    .build();

            communityResponse.setRelationInfo(member, relationInfo);
        }

        return communityResponseList;
    }

    @Transactional(readOnly = true)
    public List<CommunityScrapResponse> setScrapRelationInfo(List<CommunityScrapResponse> communityScrapResponseList) {
        Member member = memberService.currentMember();
        CommunityRelationInfo relationInfo = new CommunityRelationInfo();
        if (member == null) {
            for (CommunityScrapResponse communityScrapResponse : communityScrapResponseList) {
                communityScrapResponse.setRelationInfo(relationInfo);
            }
            return communityScrapResponseList;
        }

        List<Long> communityIds = communityScrapResponseList.stream()
                .map(CommunityScrapResponse::getCommunityId)
                .collect(Collectors.toList());

        List<Long> writerIds = communityScrapResponseList.stream()
                .map(CommunityScrapResponse::getMember)
                .collect(Collectors.toList()).stream()
                .map(CommunityMemberResponse::getId)
                .collect(Collectors.toList());

        Map<Long, CommunityLike> likeMap = getLikeMap(member.getId(), communityIds);
        Map<Long, CommunityReport> reportMap = getReportMap(member.getId(), communityIds);
        Map<Long, Block> blockMap = getBlockMap(member.getId(), writerIds);

        for (CommunityScrapResponse communityScrapResponse : communityScrapResponseList) {
            relationInfo = CommunityRelationInfo.builder()
                    .isLike(likeMap.containsKey(communityScrapResponse.getCommunityId()))
                    .isReport(reportMap.containsKey(communityScrapResponse.getCommunityId()))
                    .isBlock(communityScrapResponse.getCategory().getType() != CommunityCategoryType.BLIND && blockMap.containsKey(communityScrapResponse.getMember().getId()))
                    .build();

            communityScrapResponse.setRelationInfo(relationInfo);
        }

        return communityScrapResponseList;
    }





    private Map<Long, CommunityLike> getLikeMap(long memberId, List<Long> communityIds) {
        List<CommunityLike> likeCommunities = likeDao.likeCommunities(memberId, communityIds);
        return likeCommunities.stream()
                .collect(Collectors.toMap(CommunityLike::getCommunityId, Function.identity()));
    }

    private Map<Long, CommunityReport> getReportMap(long memberId, List<Long> communityIds) {
        List<CommunityReport> reportCommunities = reportDao.reportCommunities(memberId, communityIds);
        return reportCommunities.stream()
                .collect(Collectors.toMap(CommunityReport::getCommunityId, Function.identity()));
    }

    private Map<Long, Block> getBlockMap(long memberId, List<Long> writerIds) {
        List<Block> blockList = blockDao.isBlock(memberId, writerIds);
        return blockList.stream()
                .collect(Collectors.toMap(Block::getYouId, Function.identity()));
    }
}
