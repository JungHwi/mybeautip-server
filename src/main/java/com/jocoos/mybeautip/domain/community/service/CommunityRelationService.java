package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.dto.MemberResponse;
import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityLike;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityReport;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityLikeDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityReportDao;
import com.jocoos.mybeautip.domain.community.vo.CommunityRelationInfo;
import com.jocoos.mybeautip.domain.member.service.dao.MemberBlockDao;
import com.jocoos.mybeautip.domain.scrap.persistence.domain.Scrap;
import com.jocoos.mybeautip.domain.scrap.service.dao.ScrapDao;
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

import static com.jocoos.mybeautip.domain.scrap.code.ScrapType.COMMUNITY;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommunityRelationService {
    private final LegacyMemberService memberService;
    private final CommunityLikeDao likeDao;
    private final CommunityReportDao reportDao;
    private final MemberBlockDao blockDao;
    private final ScrapDao scrapDao;

    @Transactional(readOnly = true)
    public CommunityResponse setRelationInfo(CommunityResponse communityResponse) {
        Member member = memberService.currentMember();
        if (member == null) {
            return communityResponse.setRelationInfo(CommunityRelationInfo.withIsScrap());
        }

        CommunityRelationInfo relationInfo = CommunityRelationInfo.builder()
                .isLike(likeDao.isLike(member.getId(), communityResponse.getId()))
                .isReport(reportDao.isReport(member.getId(), communityResponse.getId()))
                .isBlock(communityResponse.getCategory().getType() != CommunityCategoryType.BLIND && blockDao.isBlock(member.getId(), communityResponse.getMember().getId()))
                .isScrap(scrapDao.isScrap(COMMUNITY, member.getId(), communityResponse.getId()))
                .build();

        return communityResponse.setRelationInfo(member, relationInfo);
    }

    @Transactional(readOnly = true)
    public List<CommunityResponse> setRelationInfo(List<CommunityResponse> communityResponseList) {
        Member member = memberService.currentMember();
        CommunityRelationInfo relationInfo = CommunityRelationInfo.withIsScrap();
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
                .map(MemberResponse::getId)
                .collect(Collectors.toList());

        Map<Long, CommunityLike> likeMap = getLikeMap(member.getId(), communityIds);
        Map<Long, CommunityReport> reportMap = getReportMap(member.getId(), communityIds);
        Map<Long, Block> blockMap = getBlockMap(member.getId(), writerIds);
        Map<Long, Scrap> scrapMap = getScrapMap(member.getId(), communityIds);

        for (CommunityResponse communityResponse : communityResponseList) {
            relationInfo = CommunityRelationInfo.builder()
                    .isLike(likeMap.containsKey(communityResponse.getId()))
                    .isReport(reportMap.containsKey(communityResponse.getId()))
                    .isBlock(communityResponse.getCategory().getType() != CommunityCategoryType.BLIND && blockMap.containsKey(communityResponse.getMember().getId()))
                    .isScrap(scrapMap.containsKey(communityResponse.getId()))
                    .build();

            communityResponse.setRelationInfo(member, relationInfo);
            communityResponse.listBlindContent();
        }

        return communityResponseList;
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

    private Map<Long, Scrap> getScrapMap(long memberId, List<Long> communityIds) {
        List<Scrap> scraps = scrapDao.scrapCommunities(memberId, communityIds);
        return scraps.stream()
                .collect(Collectors.toMap(Scrap::getRelationId, Function.identity()));
    }
}
