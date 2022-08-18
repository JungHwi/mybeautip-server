package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.converter.CommunityCommentConverter;
import com.jocoos.mybeautip.domain.community.dto.*;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCommentDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.global.exception.AccessDeniedException;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        CommunityComment communityComment = dao.get(communityId, commentId);

        CommunityCommentResponse response = converter.convert(communityComment);

        if (community.getCategory().getType() == CommunityCategoryType.BLIND) {
            communityDao.updateSortedAt(community.getId());
        }

        return relationService.setRelationInfo(member, response);
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

        return relationService.setRelationInfo(member, response);
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

    @Transactional
    public CommunityCommentResponse edit(EditCommunityCommentRequest request) {
        Member member = legacyMemberService.currentMember();
        CommunityComment communityComment = dao.get(request.getCommunityId(), request.getCommentId());

        if (!communityComment.getMember().getId().equals(member.getId())) {
            throw new AccessDeniedException("access_denied", "This is not yours.");
        }

        communityComment.setContents(request.getContents());

        return getComment(member, communityComment);
    }

    private CommunityCommentResponse getComment(Member member, CommunityComment communityComment) {
        CommunityCommentResponse response = converter.convert(communityComment);
        return relationService.setRelationInfo(member, response);
    }

    @Transactional
    public void delete(long communityId, long commentId) {
        Member member = legacyMemberService.currentMember();
        CommunityComment communityComment = dao.get(communityId, commentId);

        if (!communityComment.getMember().getId().equals(member.getId())) {
            throw new AccessDeniedException("access_denied", "This is not yours.");
        }

        communityComment.delete();
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
