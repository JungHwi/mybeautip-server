package com.jocoos.mybeautip.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NaverMemberRepository extends JpaRepository<NaverMember, String> {
    boolean existsByNaverId(String id);

    NaverMember getByNaverId(String naverId);

    Optional<NaverMember> findByMemberId(Long memberId);
}
