package com.jocoos.mybeautip.domain.member.service;

import com.jocoos.mybeautip.domain.community.service.dao.CommunityCommentDao;
import com.jocoos.mybeautip.domain.community.service.dao.CommunityDao;
import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.converter.AdminMemberConverter;
import com.jocoos.mybeautip.domain.member.dao.MemberDao;
import com.jocoos.mybeautip.domain.member.dto.AdminMemberDetailResponse;
import com.jocoos.mybeautip.domain.member.dto.MemberStatusResponse;
import com.jocoos.mybeautip.domain.member.vo.MemberSearchResult;
import com.jocoos.mybeautip.domain.point.service.PointService;
import com.jocoos.mybeautip.domain.term.service.dao.MemberTermDao;
import com.jocoos.mybeautip.member.comment.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AdminMemberService {

    private final MemberDao memberDao;
    private final PointService pointService;
    private final CommunityDao communityDao;
    private final CommunityCommentDao communityCommentDao;
    private final CommentRepository commentRepository;
    private final MemberTermDao memberTermDao;
    private final AdminMemberConverter converter;


    @Transactional(readOnly = true)
    public List<MemberStatusResponse> getStatusesWithCount() {
        Map<MemberStatus, Long> statusCountMap = memberDao.getStatusesWithCount();
        return converter.convert(statusCountMap);
    }

    // 리팩토링 필요
    @Transactional(readOnly = true)
    public AdminMemberDetailResponse getMember(Long memberId) {
        MemberSearchResult memberWithDetails = memberDao.getMembersWithDetails(memberId);

        Long communityCount = communityDao.countBy(memberId);
        Long communityCommentCount = communityCommentDao.countBy(memberId);
        Long videoCommentCount = commentRepository.countByCreatedById(memberId);

        Long invitedFriendCount = memberDao.countInvitedFriends(memberId);
        int expiryPoint = pointService.getExpiryPoint(memberId);
        boolean isAgreeOnMarketingTerm = memberTermDao.isAgreeOnMarketingTerm(memberId);
        return converter.convert(memberWithDetails, communityCount, communityCommentCount, videoCommentCount, invitedFriendCount, expiryPoint, isAgreeOnMarketingTerm);
    }
}
