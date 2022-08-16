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
import java.util.ArrayList;
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


    public List<CommunityCommentResponse> getComments(long communityId) {
        List<CommunityCommentResponse> result = new ArrayList<>();

        CommunityRelationInfo relationInfo = CommunityRelationInfo.builder()
                .isLike(true)
                .isBlock(false)
                .isReport(false)
                .build();

        CommunityMemberResponse memberResponse = new CommunityMemberResponse(100L, MemberStatus.ACTIVE, "MockMember", DEFAULT_AVATAR_URL);

        CommunityCommentResponse item = CommunityCommentResponse.builder()
                .id(1L)
                .contents("Mock Contents 1")
                .createdAt(ZonedDateTime.now())
                .likeCount(10)
                .commentCount(3)
                .reportCount(0)
                .relationInfo(relationInfo)
                .member(memberResponse)
                .build();

        result.add(item);

        return result;
    }

    public CommunityCommentResponse getComment(long communityId, long commentId) {
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
                .commentCount(3)
                .reportCount(0)
                .createdAt(ZonedDateTime.now())
                .relationInfo(relationInfo)
                .member(memberResponse)
                .build();

        return result;
    }

    @Transactional
    public CommunityCommentResponse write(WriteCommunityCommentRequest request) {
        valid(request);
        Member member = legacyMemberService.currentMember();
        request.setMember(member);

        Community community = communityDao.get(request.getCommunityId());

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
