package com.jocoos.mybeautip.domain.member.service;

import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.converter.DormantMemberConverter;
import com.jocoos.mybeautip.domain.member.persistence.domain.DormantMember;
import com.jocoos.mybeautip.domain.member.service.dao.DormantMemberDao;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.domain.popup.dto.PopupResponse;
import com.jocoos.mybeautip.domain.popup.service.PopupService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.point.MemberPointService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.DORMANT_WAKEUP_POINT;

@Service
@RequiredArgsConstructor
public class DormantMemberService {

    private final MemberService memberService;
    private final MemberPointService memberPointService;
    private final PopupService popupService;
    private final MemberDao memberDao;
    private final DormantMemberDao dormantMemberDao;
    private final DormantMemberConverter dormantMemberConverter;

    @Transactional
    public int changeDormantMember() {
        int result = 0;
        List<Member> members = memberDao.getDormantTarget();
        for (Member member : members) {
            this.toDormantMember(member);
            result++;
        }

        return result;
    }

    @Transactional
    public PopupResponse wakeup(long memberId) {
        Member member = toMember(memberId);
        memberPointService.earnPoint(member, DORMANT_WAKEUP_POINT);
        return popupService.getWakeupPopup();
    }

    public Member toDormantMember(Member member) {
        dormantMemberDao.changeToDormantMember(member);
        return member.changeStatus(MemberStatus.DORMANT);
    }

    private Member toMember(long memberId) {
        Member member = memberDao.getMember(memberId);
        DormantMember dormantMember = dormantMemberDao.getDormantMember(memberId);
        member = dormantMemberConverter.merge(dormantMember, member);
        dormantMemberDao.deleteDormantMember(dormantMember);
        return memberService.adjustUniqueInfo(member);
    }
}
