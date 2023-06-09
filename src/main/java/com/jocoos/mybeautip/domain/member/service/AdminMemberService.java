package com.jocoos.mybeautip.domain.member.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

import com.jocoos.mybeautip.domain.community.dto.MemberResponse;
import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.converter.AdminMemberConverter;
import com.jocoos.mybeautip.domain.member.dto.*;
import com.jocoos.mybeautip.domain.member.persistence.domain.UsernameCombinationWord;
import com.jocoos.mybeautip.domain.member.service.dao.*;
import com.jocoos.mybeautip.domain.member.vo.MemberBasicSearchResult;
import com.jocoos.mybeautip.domain.member.vo.MemberSearchCondition;
import com.jocoos.mybeautip.domain.member.vo.MemberSearchResult;
import com.jocoos.mybeautip.domain.operation.code.OperationTargetType;
import com.jocoos.mybeautip.domain.operation.code.OperationType;
import com.jocoos.mybeautip.domain.operation.converter.OperationLogConverter;
import com.jocoos.mybeautip.domain.operation.dto.OperationLogRequest;
import com.jocoos.mybeautip.domain.operation.service.dao.OperationLogDao;
import com.jocoos.mybeautip.domain.point.dao.MemberPointDao;
import com.jocoos.mybeautip.domain.point.service.PointReasonService;
import com.jocoos.mybeautip.domain.point.service.PointService;
import com.jocoos.mybeautip.domain.report.service.dao.ContentReportDao;
import com.jocoos.mybeautip.domain.term.persistence.domain.MemberTerm;
import com.jocoos.mybeautip.domain.term.service.dao.MemberTermDao;
import com.jocoos.mybeautip.global.wrapper.PageResponse;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.point.MemberPoint;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AdminMemberService {

    private final MemberDao memberDao;
    private final DormantMemberDao dormantMemberDao;
    private final OperationLogDao operationLogDao;
    private final UsernameCombinationWordDao usernameDao;
    private final PointService pointService;
    private final PointReasonService pointReasonService;
    private final MemberTermDao memberTermDao;
    private final MemberPointDao memberPointDao;
    private final ContentReportDao contentReportDao;
    private final JwtDao jwtDao;
    private final AdminMemberConverter converter;
    private final OperationLogConverter operationLogConverter;
    private final MemberActivityCountDao memberActivityCountDao;

    @Transactional(readOnly = true)
    public List<MemberStatusResponse> getStatusesWithCount() {
        Map<MemberStatus, Long> statusCountMap = memberDao.getStatusesWithCount();
        return converter.convert(statusCountMap);
    }

    // 리팩토링 필요
    @Transactional(readOnly = true)
    public AdminMemberDetailResponse getMember(Long memberId) {
        MemberSearchResult memberWithDetails = memberDao.getMembersWithDetails(memberId);

        Long invitedFriendCount = memberDao.countInvitedFriends(memberId);
        int expiryPoint = pointService.getExpiryPoint(memberId);
        boolean isAgreeOnMarketingTerm = memberTermDao.isAgreeOnMarketingTerm(memberId);
        return converter.convert(memberWithDetails, invitedFriendCount, expiryPoint, isAgreeOnMarketingTerm);
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminMemberPointResponse> getPointHistory(Long memberId, Pageable pageable) {
        Page<MemberPoint> page = memberPointDao.getAllBy(memberId, pageable);
        return new PageResponse<>(page.getTotalElements(), getResponseContent(page.getContent()));
    }

    private List<AdminMemberPointResponse> getResponseContent(List<MemberPoint> memberPoints) {
        pointReasonService.setReason(memberPoints);
        return converter.toPointResponse(memberPoints);
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminMemberReportResponse> getReportHistory(Long memberId, Pageable pageable) {
        return contentReportDao.getAllAccusedBy(memberId, pageable);
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminMemberResponse> getMembers(MemberSearchCondition condition) {
        Page<MemberBasicSearchResult> page = memberDao.getMembers(condition);
        setIsAgreeMarketingTerm(page.getContent());
        setReportCount(page.getContent());
        List<AdminMemberResponse> content = converter.toListResponse(page.getContent());
        return new PageResponse<>(page.getTotalElements(), content);
    }

    public List<UsernameCombinationWord> refreshUsername() {
        return usernameDao.refresh();
    }

    private void setIsAgreeMarketingTerm(List<MemberBasicSearchResult> content) {
        List<Long> memberIds = MemberBasicSearchResult.memberIds(content);
        List<MemberTerm> memberTerms = memberTermDao.isAgreeMarketingTerm(memberIds);
        Map<Long, Boolean> agreeTermMap = MemberTerm.memberIdIsAgreeTermMap(memberTerms);
        MemberBasicSearchResult.setIsAgreeMarketingTerm(content, agreeTermMap);
    }

    private void setReportCount(List<MemberBasicSearchResult> content) {
        List<Long> ids = MemberBasicSearchResult.memberIds(content);
        Map<Long, Integer> idCountMap = contentReportDao.getAllReportCountMap(ids);
        content.forEach(c -> c.setReportCount(idCountMap.getOrDefault(c.getId(), 0)));
    }


    @Transactional
    public int offSuspendedMember() {
        int result = 0;
        List<Member> members = memberDao.getSuspendedTarget();
        for (Member member : members) {
            MemberStatus memberStatus = member.getStatus();
            member.changeStatus(MemberStatus.ACTIVE);

            OperationLogRequest logRequest = OperationLogRequest.builder()
                    .targetId(String.valueOf(member.getId()))
                    .targetType(OperationTargetType.MEMBER)
                    .operationType(OperationType.MEMBER_SUSPENDED_OFF)
                    .description(String.format("%s -> %s", memberStatus.name(), MemberStatus.ACTIVE.name()))
                    .build();

            operationLogDao.logging(logRequest);
            result++;
        }

        return result;
    }

    @Transactional
    public int deleteExpiredRefreshToken() {
        return jwtDao.deleteExpiredJwt();
    }

    @Transactional
    public Member updateStatus(MemberStatusRequest request) {
        Member adminMember = memberDao.getMember(request.getAdminId());
        request.setAdminMember(adminMember);
        Member member = memberDao.getMember(request.getMemberId());
        request.setBeforeStatus(member.getStatus());
        OperationLogRequest logRequest = operationLogConverter.converts(request);
        operationLogDao.logging(logRequest);
        return memberDao.updateStatus(member, request.getAfterStatus());
    }

    @Transactional
    public MemberResponse saveOrUpdate(MemberRegistrationRequest request) {
        Member member = memberDao.saveOrUpdate(request);

        if (!memberActivityCountDao.existsById(member.getId())) {
            memberActivityCountDao.init(member);
        }

        return converter.convert(member);
    }
}
