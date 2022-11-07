package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.jocoos.mybeautip.domain.member.persistence.domain.MemberMemo;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;
import com.jocoos.mybeautip.member.Member;

import java.util.Optional;

public interface MemberMemoRepository extends DefaultJpaRepository<MemberMemo, Long> {
    Optional<MemberMemo> findByMember(Member member);
}
