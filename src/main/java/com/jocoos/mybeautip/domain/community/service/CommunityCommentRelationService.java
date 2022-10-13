package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.dto.CommunityCommentResponse;
import com.jocoos.mybeautip.domain.community.dto.CommunityMemberResponse;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCategory;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCommentLike;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCommentReport;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCategoryDao;
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

    private final CommunityCategoryDao categoryDao;
    private final CommunityCommentLikeDao likeDao;
    private final CommunityCommentReportDao reportDao;
    private final MemberBlockDao blockDao;

    @Transactional(readOnly = true)
    public CommunityCommentResponse setRelationInfo(Member member, CommunityCommentResponse communityCommentResponse) {
        CommunityCategory category = categoryDao.getCommunityCategory(communityCommentResponse.getCategoryId());
        if (member == null) {
            return communityCommentResponse.setRelationInfo(new CommunityRelationInfo(), category.getType());
        }

        CommunityRelationInfo relationInfo = CommunityRelationInfo.builder()
                .isLike(likeDao.isLike(member.getId(), communityCommentResponse.getId()))
                .isReport(reportDao.isReport(member.getId(), communityCommentResponse.getId()))
                .isBlock(category.getType() != CommunityCategoryType.BLIND && blockDao.isBlock(member.getId(), communityCommentResponse.getMember().getId()))
                .build();

        return communityCommentResponse.setRelationInfo(member, relationInfo, category.getType());
    }

    @Transactional(readOnly = true)
    public List<CommunityCommentResponse> setRelationInfo(Member member, List<CommunityCommentResponse> communityCommentResponses) {
        CommunityRelationInfo relationInfo = new CommunityRelationInfo();

        List<Long> categoryIds = communityCommentResponses.stream()
                .map(CommunityCommentResponse::getCategoryId)
                .collect(Collectors.toList());

        Map<Long, CommunityCategoryType> categoryTypeMap = getCategoryMap(communityCommentResponses, categoryIds);

        if (member == null) {
            for (CommunityCommentResponse communityCommentResponse : communityCommentResponses) {
                communityCommentResponse.setRelationInfo(relationInfo, categoryTypeMap.get(communityCommentResponse.getId()));
            }
            return communityCommentResponses;
        }

        List<Long> commentIds = communityCommentResponses.stream()
                .map(CommunityCommentResponse::getId)
                .collect(Collectors.toList());

        List<Long> writerIds = communityCommentResponses.stream()
                .map(CommunityCommentResponse::getMember)
                .collect(Collectors.toList()).stream()
                .map(CommunityMemberResponse::getId)
                .collect(Collectors.toList());

        Map<Long, CommunityCommentLike> likeMap = getLikeMap(member.getId(), commentIds);
        Map<Long, CommunityCommentReport> reportMap = getReportMap(member.getId(), commentIds);
        Map<Long, Block> blockMap = getBlockMap(member.getId(), writerIds);

        for (CommunityCommentResponse communityCommentResponse : communityCommentResponses) {
            relationInfo = CommunityRelationInfo.builder()
                    .isLike(likeMap.containsKey(communityCommentResponse.getId()))
                    .isReport(reportMap.containsKey(communityCommentResponse.getId()))
                    .isBlock(categoryTypeMap.get(communityCommentResponse.getId()) != CommunityCategoryType.BLIND && blockMap.containsKey(communityCommentResponse.getMember().getId()))
                    .build();

            communityCommentResponse.setRelationInfo(member, relationInfo, categoryTypeMap.get(communityCommentResponse.getId()));
        }

        return communityCommentResponses;
    }

    private Map<Long, CommunityCategoryType> getCategoryMap(List<CommunityCommentResponse> communityCommentResponses, List<Long> categoryIds) {
        List<CommunityCategory> categoryList = categoryDao.getCommunityCategory(categoryIds);
        Map<Long, CommunityCategoryType> communityCategoryTypeMap = categoryList.stream()
                        .collect(Collectors.toMap(CommunityCategory::getId, CommunityCategory::getType));

        return communityCommentResponses.stream()
                .collect(Collectors.toMap(CommunityCommentResponse::getId, commentResponse -> communityCategoryTypeMap.get(commentResponse.getCategoryId())));
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
