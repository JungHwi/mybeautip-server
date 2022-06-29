package com.jocoos.mybeautip.domain.member.service;

import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public Member findById(long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("No such member. id - " + id));
    }
}
