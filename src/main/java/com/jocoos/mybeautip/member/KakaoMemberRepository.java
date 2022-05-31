package com.jocoos.mybeautip.member;

import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

public interface KakaoMemberRepository extends PagingAndSortingRepository<KakaoMember, String> {

    boolean existsByKakaoId(String kakaoId);

    Optional<KakaoMember> findByMemberId(Long memberId);
}
