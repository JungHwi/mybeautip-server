package com.jocoos.mybeautip.member;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface AppleMemberRepository extends PagingAndSortingRepository<AppleMember, String> {

    AppleMember getByAppleId(String appleId);

    boolean existsByAppleId(String appleId);

    Optional<AppleMember> findByMemberId(Long memberId);
}
