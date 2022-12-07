package com.jocoos.mybeautip.domain.zzz_test.service;

import com.jocoos.mybeautip.domain.member.service.dao.MemberDao;
import com.jocoos.mybeautip.domain.member.service.social.DormantMemberService;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TestService {
    private final DormantMemberService dormantMemberService;
    private final MemberDao memberDao;

    @Transactional
    public void toDormantMember(long memberId) {
        Member member = memberDao.getMember(memberId);
        dormantMemberService.toDormantMember(member);
    }
}
