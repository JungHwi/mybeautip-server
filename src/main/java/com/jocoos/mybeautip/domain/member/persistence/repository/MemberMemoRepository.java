package com.jocoos.mybeautip.domain.member.persistence.repository;

import com.jocoos.mybeautip.domain.member.persistence.domain.MemberMemo;
import com.jocoos.mybeautip.global.config.jpa.DefaultJpaRepository;

import java.util.Optional;

public interface MemberMemoRepository extends DefaultJpaRepository<MemberMemo, Long> {
    Optional<MemberMemo> findByIdAndTargetId(Long memoId, Long memberId);
}
