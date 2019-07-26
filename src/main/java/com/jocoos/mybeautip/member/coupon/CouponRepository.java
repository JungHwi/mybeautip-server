package com.jocoos.mybeautip.member.coupon;

import java.util.Date;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

  Optional<Coupon> findByCategoryAndStartedAtBeforeAndEndedAtAfter(Byte category, Date startedAt, Date endedAt);

}
