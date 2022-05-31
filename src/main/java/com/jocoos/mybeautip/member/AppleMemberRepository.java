package com.jocoos.mybeautip.member;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface AppleMemberRepository extends PagingAndSortingRepository<AppleMember, String> {

    boolean existsByAppleId(String appleId);

    Optional<AppleMember> findByMemberId(Long memberId);
}
