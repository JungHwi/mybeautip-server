package com.jocoos.mybeautip.admin;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.coupon.Coupon;
import com.jocoos.mybeautip.member.coupon.CouponRepository;
import com.jocoos.mybeautip.member.coupon.CouponService;
import com.jocoos.mybeautip.member.coupon.MemberCouponRepository;

@Slf4j
@RestController
@RequestMapping("/api/admin/manual")
public class AdminCouponController {

  private final MemberRepository memberRepository;
  private final CouponRepository couponRepository;
  private final MemberCouponRepository memberCouponRepository;
  private final CouponService couponService;

  public AdminCouponController(MemberRepository memberRepository,
                               CouponRepository couponRepository,
                               MemberCouponRepository memberCouponRepository,
                               CouponService couponService) {
    this.memberRepository = memberRepository;
    this.couponRepository = couponRepository;
    this.memberCouponRepository = memberCouponRepository;
    this.couponService = couponService;
  }

  @PostMapping("/coupons")
  public ResponseEntity<SendCouponResponse> sendCouponToMember(@RequestBody SendCouponRequest request) {

    Coupon coupon = couponRepository.findById(request.getCouponId())
        .orElseThrow(() -> new NotFoundException("coupon_not_found", "invalid coupon id"));

    log.info("{}", coupon);

    if (request.getMemberId() == null) {
      throw new IllegalArgumentException("member id not found");
    }

    List<Member> availables = Lists.newArrayList();
    if (request.getMemberId() == 0L) {
      PageRequest pageRequest = PageRequest.of(0, Integer.MAX_VALUE);
      availables.addAll(memberRepository.findByVisibleAndDeletedAtIsNull(true, pageRequest));
    } else {
      Member m = memberRepository.findByIdAndVisibleIsTrue(request.getMemberId())
          .orElseThrow(() -> new NotFoundException("member_not_found", "invalid member id or not visible"));
      availables.add(m);
    }

    for (Member m : availables) {
      log.info("send coupon[{}] to member[{}]", coupon.getId(), m.getId());
      couponService.sendEventCoupon(m, coupon);
    }

    return new ResponseEntity<>(new SendCouponResponse(availables.size()), HttpStatus.OK);
  }


  @Data
  @NoArgsConstructor
  static class SendCouponRequest {
    @NotNull
    private Long couponId;

    /**
     * If set memberId to 0, send the coupon to every member
     */
    @NotNull
    private Long memberId;

    private Date expiryAt;
  }

  @Data
  @AllArgsConstructor
  static class SendCouponResponse {
    int count;
  }
}
