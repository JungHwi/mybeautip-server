package com.jocoos.mybeautip.member.billing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberBillingAuthRepository extends JpaRepository<MemberBillingAuth, Long> {
  Optional<MemberBillingAuth> findTopByMemberIdOrderByCreatedAtDesc(Long memberId);
  void deleteTopByMemberIdOrderByCreatedAtDesc(Long memberId);
}
