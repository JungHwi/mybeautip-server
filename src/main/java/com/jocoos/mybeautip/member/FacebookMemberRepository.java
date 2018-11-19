package com.jocoos.mybeautip.member;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FacebookMemberRepository extends JpaRepository<FacebookMember, String> {
  Optional<FacebookMember> findByMemberId(Long memberId);
}
