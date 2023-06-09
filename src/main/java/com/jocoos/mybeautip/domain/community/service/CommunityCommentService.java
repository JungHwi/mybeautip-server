package com.jocoos.mybeautip.domain.community.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.community.code.CommunityCategoryType;
import com.jocoos.mybeautip.domain.community.converter.CommunityCommentConverter;
import com.jocoos.mybeautip.domain.community.dto.*;
import com.jocoos.mybeautip.domain.community.persistence.domain.Community;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityComment;
import com.jocoos.mybeautip.domain.community.persistence.domain.CommunityCommentLike;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCommentDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCommentLikeDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityCommentReportDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.member.dto.MyCommunityCommentResponse;
import com.jocoos.mybeautip.domain.member.service.dao.MemberActivityCountDao;
import com.jocoos.mybeautip.domain.notification.aspect.annotation.SendNotification;
import com.jocoos.mybeautip.domain.notification.code.TemplateType;
import com.jocoos.mybeautip.domain.point.service.ActivityPointService;
import com.jocoos.mybeautip.global.exception.AccessDeniedException;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.global.vo.Files;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.domain.notification.code.TemplateType.*;
import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.*;
import static com.jocoos.mybeautip.domain.point.service.activity.ValidObject.validDomainAndReceiver;
import static com.jocoos.mybeautip.global.code.UrlDirectory.COMMUNITY_COMMENT;

@Service
@RequiredArgsConstructor
public class CommunityCommentService {

    private final CommunityCommentRelationService relationService;
    private final CommunityDao communityDao;
    private final CommunityCommentDao dao;
    private final CommunityCommentLikeDao likeDao;
    private final CommunityCommentReportDao reportDao;
    private final LegacyMemberService legacyMemberService;
    private final MemberActivityCountDao activityCountDao;
    private final ActivityPointService activityPointService;
    private final CommunityCommentDeleteService deleteService;
    private final CommunityCommentConverter converter;
    private final CommunityCommentCRUDService crudService;
    private final AwsS3Handler awsS3Handler;

    @Transactional(readOnly = true)
    public List<CommunityCommentResponse> getComments(SearchCommentRequest request) {
        Member member = legacyMemberService.currentMember();
        request.setMemberId(member != null ? member.getId() : null);
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

    @SendNotification(templateTypes = {TemplateType.COMMUNITY_COMMENT, COMMUNITY_COMMENT_REPLY})
    @Transactional
    public CommunityCommentResponse write(WriteCommunityCommentRequest request) {
        Member member = request.getMember();
        CommunityComment communityComment = crudService.write(request);

        CommunityCommentResponse response = converter.convert(communityComment);
        activityPointService.gainActivityPoint(WRITE_COMMUNITY_COMMENT,
                validDomainAndReceiver(communityComment, communityComment.getId(), member));
        activityCountDao.updateAllCommunityCommentCount(member, 1);
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
            throw new BadRequestException("Not matched Community Info. Parent comment's community id - " + parentComment.getCommunityId());
        }
    }

    @Transactional
    public CommunityCommentResponse edit(EditCommunityCommentRequest request) {
        Member requestMember = request.getMember();
        CommunityComment communityComment = dao.get(request.getCommunityId(), request.getCommentId());
        Files editedFiles = request.fileDtoToFiles(communityComment.getFileUrl());

        communityComment.edit(request.getContents(), editedFiles, requestMember);
        dao.save(communityComment);

        awsS3Handler.editFiles(request.getFiles(), COMMUNITY_COMMENT.getDirectory(communityComment.getId()));
        return convertToResponse(requestMember, communityComment);
    }

    private CommunityCommentResponse convertToResponse(Member member, CommunityComment communityComment) {
        CommunityCommentResponse response = converter.convert(communityComment);
        return relationService.setRelationInfo(member, response);
    }

    @Transactional
    public void delete(long communityId, long commentId) {
        Member member = legacyMemberService.currentMember();
        CommunityComment communityComment = dao.get(communityId, commentId);

        if (!communityComment.getMember().getId().equals(member.getId())) {
            throw new AccessDeniedException(ErrorCode.ACCESS_DENIED, "This is not yours.");
        }

        deleteService.delete(communityComment);
        activityPointService.retrieveActivityPoint(DELETE_COMMUNITY_COMMENT, communityComment.getId(), member);
    }

    @Transactional
    public LikeResponse like(long commentId, boolean isLike) {
        Member member = legacyMemberService.currentMember();
        CommunityCommentLike like = likeDao.like(member.getId(), commentId, isLike);

        CommunityComment comment = dao.get(commentId);

        gainActivityPoint(isLike, comment.getMember(), like);

        return LikeResponse.builder()
                .isLike(isLike)
                .likeCount(comment.getLikeCount())
                .build();
    }

    private void gainActivityPoint(boolean isLike, Member receiver, CommunityCommentLike like) {
        if (isLike) {
            activityPointService.gainActivityPoint(GET_LIKE_COMMUNITY_COMMENT,
                    validDomainAndReceiver(like, like.getId(), receiver));
        }
    }

    @Transactional
    public ReportResponse report(long commentId, ReportRequest report) {
        long memberId = legacyMemberService.currentMemberId();

        CommunityComment comment = dao.get(commentId);
        reportDao.report(memberId, comment.getMemberId(), commentId, report);

        if (comment.getMemberId() == memberId) {
            throw new BadRequestException("this is my comment.");
        }

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
