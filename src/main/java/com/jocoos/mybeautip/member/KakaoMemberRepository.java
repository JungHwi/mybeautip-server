package com.jocoos.mybeautip.member;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface KakaoMemberRepository extends PagingAndSortingRepository<KakaoMember, String> {
    Optional<KakaoMember> findByMemberId(Long memberId);
}
