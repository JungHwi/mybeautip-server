package com.jocoos.mybeautip.member.billing;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberBillingRepository extends JpaRepository<MemberBilling, Long> {
  Optional<MemberBilling> findByIdAndMemberIdAndValid(Long id, Long memberId, Boolean valid);

  List<MemberBilling> findByMemberIdAndValid(Long memberId, Boolean valid);

  Optional<MemberBilling> findTopByMemberIdAndValidIsFalse(Long memberId);

  Optional<MemberBilling> findTopByMemberIdAndValidIsTrueAndBaseIsTrue(Long memberId);

  List<MemberBilling> findByMemberIdAndBaseIsTrue(Long memberId);
}
