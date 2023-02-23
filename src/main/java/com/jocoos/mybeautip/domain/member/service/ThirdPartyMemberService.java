package com.jocoos.mybeautip.domain.member.service;

import com.jocoos.mybeautip.client.flipfloplite.FlipFlopLiteService;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ThirdPartyMemberService {

    private final FlipFlopLiteService fflService;

    public void integrateThirdPartyMember(Member member) {
        fflService.login(member);
    }
}
