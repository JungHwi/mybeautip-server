package com.jocoos.mybeautip.domain.zzz_test.service;

import com.jocoos.mybeautip.domain.member.service.DormantMemberService;
import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.domain.operation.persistence.repository.OperationLogRepository;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.jocoos.mybeautip.domain.member.code.MemberStatus.ACTIVE;

@Service
@RequiredArgsConstructor
public class TestService {
    private final DormantMemberService dormantMemberService;
    private final MemberDao memberDao;
    private final OperationLogRepository OperationLogRepository;

    @Transactional
    public void toDormantMember(long memberId) {
        Member member = memberDao.getMember(memberId);
        dormantMemberService.toDormantMember(member);
    }

    @Transactional
    public void toActiveMember(long memberId) {
        Member member = memberDao.getMember(memberId);
        switch (member.getStatus()) {
            case DORMANT -> dormantMemberService.wakeup(memberId);
            default -> {
                member.setStatus(ACTIVE);
                OperationLogRepository.deleteByTargetId(String.valueOf(member.getId()));
            }
        }
    }
}
