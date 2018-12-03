package com.jocoos.mybeautip.admin;

import java.util.Date;

import org.springframework.data.rest.core.config.Projection;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.coupon.Coupon;
import com.jocoos.mybeautip.member.coupon.MemberCoupon;

@Projection(name="coupon_detail", types = MemberCoupon.class)
public interface MemberCouponExceprt {

  Long getId();

  Member getMember();

  Coupon getCoupon();

  Date getCreatedAt();

  Date getUsedAt();
}
