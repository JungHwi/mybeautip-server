package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.jocoos.mybeautip.domain.member.persistence.domain.MemberMemo;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;

import java.util.Optional;

public interface MemberMemoRepository extends DefaultJpaRepository<MemberMemo, Long> {
    Optional<MemberMemo> findByIdAndMemberId(Long memoId, Long memberId);
    Optional<Long> deleteByIdAndMemberIdAndCreatedById(Long memoId, Long memberId, Long deleteById);
}
