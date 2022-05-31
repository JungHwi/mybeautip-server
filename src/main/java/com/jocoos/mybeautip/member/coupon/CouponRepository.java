package com.jocoos.mybeautip.member.coupon;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.Optional;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCategoryAndStartedAtBeforeAndEndedAtAfter(Byte category, Date startedAt, Date endedAt);

}
