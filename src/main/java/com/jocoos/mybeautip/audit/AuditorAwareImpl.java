package com.jocoos.mybeautip.audit;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Slf4j
public class AuditorAwareImpl implements AuditorAware<Member> {

    @Autowired
    private MemberService memberService;

    @Override
    public Optional<Member> getCurrentAuditor() {
        return Optional.ofNullable(memberService.currentMember());
    }
}
