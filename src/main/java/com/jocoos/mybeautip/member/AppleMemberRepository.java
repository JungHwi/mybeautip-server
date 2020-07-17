package com.jocoos.mybeautip.member;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface AppleMemberRepository extends PagingAndSortingRepository<AppleMember, String> {
  Optional<AppleMember> findByMemberId(Long memberId);
}
