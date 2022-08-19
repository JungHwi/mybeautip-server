package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.converter.CommunityCommentConverter;
import com.jocoos.mybeautip.domain.community.dto.*;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCommentDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCommentLikeDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCommentReportDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.member.dto.MyCommunityCommentResponse;
import com.jocoos.mybeautip.domain.point.service.ActivityPointService;
import com.jocoos.mybeautip.global.exception.AccessDeniedException;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.WRITE_COMMUNITY_COMMENT;
import static com.jocoos.mybeautip.domain.point.util.ValidObject.validDomainAndReceiver;

@Service
@RequiredArgsConstructor
public class CommunityCommentService {

    private final CommunityCommentRelationService relationService;

    private final CommunityDao communityDao;
    private final CommunityCommentDao dao;
    private final CommunityCommentLikeDao likeDao;
    private final CommunityCommentReportDao reportDao;
    private final LegacyMemberService legacyMemberService;

    private final ActivityPointService activityPointService;

    private final CommunityCommentConverter converter;

    @Transactional(readOnly = true)
    public List<CommunityCommentResponse> getComments(SearchCommentRequest request) {
        Member member = legacyMemberService.currentMember();

        List<CommunityComment> communityComments = dao.getComments(request);

        return getComments(member, communityComments);
    }

    @Transactional(readOnly = true)
    public List<MyCommunityCommentResponse> getMyComments(long cursor, Pageable pageable) {
        Member member = legacyMemberService.currentMember();

        List<CommunityComment> communityComments = dao.getMyComments(member.getId(), cursor, pageable);
        List<MyCommunityCommentResponse> responses = converter.convertToMyComment(communityComments);
        for (MyCommunityCommentResponse response : responses) {
            response.changeContents();
        }

        return responses;
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

        activityPointService.gainActivityPoint(WRITE_COMMUNITY_COMMENT, validDomainAndReceiver(communityComment, member));

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
        activityPointService.retrieveActivityPoint(WRITE_COMMUNITY_COMMENT, communityComment.getId(), member);
    }

    @Transactional
    public LikeResponse like(long commentId, boolean isLike) {
        long memberId = legacyMemberService.currentMemberId();
        likeDao.like(memberId, commentId, isLike);

        CommunityComment comment = dao.get(commentId);

        return LikeResponse.builder()
                .isLike(isLike)
                .likeCount(comment.getLikeCount())
                .build();
    }

    @Transactional
    public ReportResponse report(long commentId, ReportRequest report) {
        long memberId = legacyMemberService.currentMemberId();
        CommunityComment comment = dao.get(commentId);
        if (comment.getMemberId() == memberId) {
            throw new BadRequestException("this is my comment.");
        }

        reportDao.report(memberId, commentId, report);

        return ReportResponse.builder()
                .isReport(report.getIsReport())
                .reportCount(comment.getReportCount())
                .build();
    }

    @Transactional(readOnly = true)
    public ReportResponse isReport(long commentId) {
        Long memberId = legacyMemberService.currentMemberId();

        boolean isReport = reportDao.isReport(memberId, commentId);

        CommunityComment comment = dao.get(commentId);

        return ReportResponse.builder()
                .isReport(isReport)
                .reportCount(comment.getReportCount())
                .build();
    }
}
