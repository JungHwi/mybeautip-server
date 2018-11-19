package com.jocoos.mybeautip.member;

import java.util.Optional;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface KakaoMemberRepository extends PagingAndSortingRepository<KakaoMember, String> {
  Optional<KakaoMember> findByMemberId(Long memberId);
}
