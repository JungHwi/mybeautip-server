package com.jocoos.mybeautip.audit;

import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

@Slf4j
public class AuditorAwareImpl implements AuditorAware<Member> {

    @Autowired
    private LegacyMemberService legacyMemberService;

    @Override
    public Optional<Member> getCurrentAuditor() {
        return Optional.ofNullable(legacyMemberService.currentMember());
    }
}
