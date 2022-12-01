package com.jocoos.mybeautip.domain.member.service;

import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.converter.AdminMemberConverter;
import com.jocoos.mybeautip.domain.member.dto.*;
import com.jocoos.mybeautip.domain.member.persistence.domain.UsernameCombinationWord;
import com.jocoos.mybeautip.domain.member.service.dao.DormantMemberDao;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.domain.member.service.dao.UsernameCombinationWordDao;
import com.jocoos.mybeautip.domain.member.vo.MemberBasicSearchResult;
import com.jocoos.mybeautip.domain.member.vo.MemberSearchCondition;
import com.jocoos.mybeautip.domain.member.vo.MemberSearchResult;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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
    private final AdminMemberConverter converter;
    private final OperationLogConverter operationLogConverter;


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

    @Transactional
    public Long updateMemo(Long memberId, String memo) {
        memberDao.updateMemberMemo(memberId, memo);
        return memberId;
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminMemberReportResponse> getReportHistory(Long memberId, Pageable pageable) {
        return contentReportDao.getAllAccusedBy(memberId, pageable);
    }

    @Transactional(readOnly = true)
    public PageResponse<AdminMemberResponse> getMembers(MemberSearchCondition condition) {
        Page<MemberBasicSearchResult> page = memberDao.getMembers(condition);
        setIsAgreeMarketingTerm(page.getContent());
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

    @Transactional
    public int changeDormantMember() {
        int result = 0;
        List<Member> members = memberDao.getDormantTarget();
        for (Member member : members) {
            dormantMemberDao.changeToDormantMember(member);
            member.changeStatus(MemberStatus.DORMANT);
            result++;
        }

        return result;
    }

    @Transactional
    public Member updateStatus(MemberStatusRequest request) {
        Member member = memberDao.getMember(request.getMemberId());
        request.setBeforeStatus(member.getStatus());
        OperationLogRequest logRequest = operationLogConverter.converts(request);
        operationLogDao.logging(logRequest);
        return memberDao.updateStatus(member, request.getAfterStatus());
    }
}
