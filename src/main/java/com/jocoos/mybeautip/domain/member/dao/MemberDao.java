package com.jocoos.mybeautip.domain.member.dao;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class MemberDao {

    private final MemberRepository repository;

    @Transactional(readOnly = true)
    public Member getMember(long memberId) {
        return repository.getById(memberId);
    }
}
