package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.dto.CommunityCommentResponse;
import com.jocoos.mybeautip.domain.community.dto.CommunityMemberResponse;
import com.jocoos.mybeautip.domain.community.dto.CommunityResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCommentLike;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCommentReport;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCommentLikeDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCommentReportDao;
import com.jocoos.mybeautip.domain.community.vo.CommunityRelationInfo;
import com.jocoos.mybeautip.domain.member.dao.MemberBlockDao;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.block.Block;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityCommentRelationService {

    private final CommunityCommentLikeDao likeDao;
    private final CommunityCommentReportDao reportDao;
    private final MemberBlockDao blockDao;

    @Transactional(readOnly = true)
    public CommunityCommentResponse setRelationInfo(Member member, Community community, CommunityCommentResponse communityCommentResponse) {
        if (member == null) {
            return communityCommentResponse.setRelationInfo(new CommunityRelationInfo());
        }

        CommunityRelationInfo relationInfo = CommunityRelationInfo.builder()
                .isLike(likeDao.isLike(member.getId(), communityCommentResponse.getId()))
                .isReport(reportDao.isReport(member.getId(), communityCommentResponse.getId()))
                .isBlock(community.getCategory().getType() != CommunityCategoryType.BLIND && blockDao.isBlock(member.getId(), communityCommentResponse.getMember().getId()))
                .build();

        return communityCommentResponse.setRelationInfo(relationInfo);
    }

    @Transactional(readOnly = true)
    public List<CommunityResponse> setRelationInfo(Member member, List<CommunityResponse> communityResponseList) {
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

        Map<Long, CommunityCommentLike> likeMap = getLikeMap(member.getId(), communityIds);
        Map<Long, CommunityCommentReport> reportMap = getReportMap(member.getId(), communityIds);
        Map<Long, Block> blockMap = getBlockMap(member.getId(), writerIds);

        for (CommunityResponse communityResponse : communityResponseList) {
            relationInfo = CommunityRelationInfo.builder()
                    .isLike(likeMap.containsKey(communityResponse.getId()))
                    .isReport(reportMap.containsKey(communityResponse.getId()))
                    .isBlock(communityResponse.getCategory().getType() != CommunityCategoryType.BLIND && blockMap.containsKey(communityResponse.getMember().getId()))
                    .build();

            communityResponse.setRelationInfo(relationInfo);
        }

        return communityResponseList;
    }

    private Map<Long, CommunityCommentLike> getLikeMap(long memberId, List<Long> commentIds) {
        List<CommunityCommentLike> likeCommunities = likeDao.likeComments(memberId, commentIds);
        return likeCommunities.stream()
                .collect(Collectors.toMap(CommunityCommentLike::getCommentId, Function.identity()));
    }

    private Map<Long, CommunityCommentReport> getReportMap(long memberId, List<Long> communityIds) {
        List<CommunityCommentReport> reportCommunities = reportDao.reportComments(memberId, communityIds);

        return reportCommunities.stream()
                .collect(Collectors.toMap(CommunityCommentReport::getCommentId, Function.identity()));
    }

    private Map<Long, Block> getBlockMap(long memberId, List<Long> writerIds) {
        List<Block> blockList = blockDao.isBlock(memberId, writerIds);
        return blockList.stream()
                .collect(Collectors.toMap(Block::getYouId, Function.identity()));
    }
}
