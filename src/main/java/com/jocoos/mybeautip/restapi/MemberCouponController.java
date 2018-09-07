package com.jocoos.mybeautip.restapi;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.coupon.Coupon;
import com.jocoos.mybeautip.member.coupon.MemberCoupon;
import com.jocoos.mybeautip.member.coupon.MemberCouponRepository;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members/me/coupons", produces = MediaType.APPLICATION_JSON_VALUE)
public class MemberCouponController {

  private final MemberService memberService;
  private final MemberCouponRepository memberCouponRepository;

  public MemberCouponController(MemberService memberService,
                                MemberCouponRepository memberCouponRepository) {
    this.memberService = memberService;
    this.memberCouponRepository = memberCouponRepository;
  }

  @GetMapping
  public ResponseEntity<List<MemberCouponInfo>> getCoupons() {
    List<MemberCouponInfo> coupons = Lists.newArrayList();
    Member member = memberService.currentMember();
    Date now = new Date();
    List<MemberCoupon> memberCoupons =
       memberCouponRepository.findByCreatedByAndCouponStartedAtBeforeAndCouponEndedAtAfter(member, now, now);
    log.debug("coupons: {}", memberCoupons);

    if (!CollectionUtils.isEmpty(memberCoupons)) {
      memberCoupons
         .stream().forEach(memberCoupon -> coupons.add(new MemberCouponInfo(memberCoupon)));
    }

    return new ResponseEntity<>(coupons, HttpStatus.OK);
  }

  @Data
  public static class MemberCouponInfo {
    private Long id;
    private CouponInfo coupon;
    private boolean used;
    private Date modifiedAt;

    public MemberCouponInfo(MemberCoupon memberCoupon) {
      BeanUtils.copyProperties(memberCoupon, this);
      this.coupon = new CouponInfo(memberCoupon.getCoupon());
    }
  }

  @Data
  public static class CouponInfo {
    private Long id;
    private String title;
    private String description;
    private String condition;
    private int discountPrice;
    private int discountRate;
    private int conditionPrice;
    private Date statedAt;
    private Date endedAt;

    public CouponInfo(Coupon coupon) {
      BeanUtils.copyProperties(coupon, this);
    }
  }
}
