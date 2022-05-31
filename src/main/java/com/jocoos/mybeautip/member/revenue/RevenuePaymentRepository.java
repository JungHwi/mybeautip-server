package com.jocoos.mybeautip.member.revenue;

import com.jocoos.mybeautip.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RevenuePaymentRepository extends JpaRepository<RevenuePayment, Long> {
    Optional<RevenuePayment> findByMemberAndDate(Member member, String date);

    Slice<RevenuePayment> findByMember(Member member, Pageable pageable);

    Slice<RevenuePayment> findByMemberAndDateLessThanEqual(Member member, String date, Pageable pageable);

    Page<RevenuePayment> findByState(int state, Pageable pageable);
}
