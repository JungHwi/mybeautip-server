package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.converter.CommunityCommentConverter;
import com.jocoos.mybeautip.domain.community.dto.*;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCommentDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.community.vo.CommunityRelationInfo;
import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.DEFAULT_AVATAR_URL;

@Service
@RequiredArgsConstructor
public class CommunityCommentService {

    private final CommunityCommentRelationService relationService;

    private final CommunityDao communityDao;
    private final CommunityCommentDao dao;
    private final LegacyMemberService legacyMemberService;

    private final CommunityCommentConverter converter;

    @Transactional(readOnly = true)
    public List<CommunityCommentResponse> getComments(SearchCommentRequest request) {
        Member member = legacyMemberService.currentMember();

        List<CommunityComment> communityComments = dao.getComments(request);

        return getComments(member, communityComments);
    }

    private List<CommunityCommentResponse> getComments(Member member, List<CommunityComment> comments) {
        List<CommunityCommentResponse> responses = converter.convert(comments);
        return relationService.setRelationInfo(member, responses);
    }

    @Transactional
    public CommunityCommentResponse getComment(long communityId, long commentId) {
        Member member = legacyMemberService.currentMember();

        Community community = communityDao.get(communityId);
        CommunityComment communityComment = dao.get(commentId);

        CommunityCommentResponse response = converter.convert(communityComment);

        if (community.getCategory().getType() == CommunityCategoryType.BLIND) {
            communityDao.updateSortedAt(community.getId());
        }

        return relationService.setRelationInfo(member, community, response);
    }

    @Transactional
    public CommunityCommentResponse write(WriteCommunityCommentRequest request) {
        valid(request);
        Member member = legacyMemberService.currentMember();
        request.setMember(member);

        Community community = communityDao.get(request.getCommunityId());
        request.setCategoryId(community.getCategoryId());

        CommunityComment communityComment = dao.write(request);

        CommunityCommentResponse response = converter.convert(communityComment);

        if (community.getCategory().getType() == CommunityCategoryType.BLIND) {
            communityDao.updateSortedAt(community.getId());
        }

        return relationService.setRelationInfo(member, community, response);
    }

    @Transactional(readOnly = true)
    public void valid(WriteCommunityCommentRequest request) {
        if (request.getParentId() != null && request.getParentId() > NumberUtils.LONG_ZERO) {
            validReply(request);
        }
    }

    @Transactional(readOnly = true)
    public void validReply(WriteCommunityCommentRequest request) {
        CommunityComment parentComment = dao.get(request.getParentId());
        if (!request.getCommunityId().equals(parentComment.getCommunityId())) {
            throw new BadRequestException("not_match_community", "Not matched Community Info. Parent comment's community id - " + parentComment.getCommunityId());
        }
    }

    public CommunityCommentResponse edit(long communityId, long commentId, WriteCommunityCommentRequest request) {
        CommunityRelationInfo relationInfo = CommunityRelationInfo.builder()
                .isLike(true)
                .isBlock(false)
                .isReport(false)
                .build();

        CommunityMemberResponse memberResponse = new CommunityMemberResponse(100L, MemberStatus.ACTIVE, "MockMember", DEFAULT_AVATAR_URL);

        CommunityCommentResponse result = CommunityCommentResponse.builder()
                .id(1L)
                .contents("Mock Contents 1")
                .likeCount(10)
                .commentCount(2)
                .reportCount(0)
                .createdAt(ZonedDateTime.now())
                .relationInfo(relationInfo)
                .member(memberResponse)
                .build();

        return result;
    }

    public void delete(long communityId, long commentId) {
        return;
    }

    public LikeResponse like(long communityId, long commentId, boolean isLike) {
        return LikeResponse.builder()
                .isLike(isLike)
                .likeCount(10)
                .build();
    }

    public void report(long communityId, long commentId, ReportRequest report) {
        return;
    }
}
