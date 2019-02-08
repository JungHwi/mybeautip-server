package com.jocoos.mybeautip.member.revenue;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jocoos.mybeautip.member.Member;

public interface RevenuePaymentRepository extends JpaRepository<RevenuePayment, Long> {
  Optional<RevenuePayment> findByMemberAndTargetDate(Member member, String targetDate);
}
