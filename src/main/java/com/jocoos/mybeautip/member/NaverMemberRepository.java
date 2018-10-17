package com.jocoos.mybeautip.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface NaverMemberRepository extends JpaRepository<NaverMember, String> {
  Optional<NaverMember> findByMemberId(Long memberId);
}
