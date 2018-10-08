package com.jocoos.mybeautip.restapi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberMeInfo;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.coupon.Coupon;
import com.jocoos.mybeautip.member.coupon.CouponService;
import com.jocoos.mybeautip.member.coupon.MemberCoupon;
import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.member.order.OrderRepository;
import com.jocoos.mybeautip.member.point.MemberPoint;
import com.jocoos.mybeautip.member.point.MemberPointRepository;
import com.jocoos.mybeautip.member.point.PointService;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members/me", produces = MediaType.APPLICATION_JSON_VALUE)
public class MemberShoppingController {

  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private final MemberService memberService;
  private final CouponService couponService;
  private final PointService pointService;

  private final OrderRepository orderRepository;
  private final MemberPointRepository memberPointRepository;

  public MemberShoppingController(MemberService memberService,
                                  CouponService couponService,
                                  PointService pointService,
                                  OrderRepository orderRepository,
                                  MemberPointRepository memberPointRepository) {
    this.memberService = memberService;
    this.couponService = couponService;
    this.pointService = pointService;
    this.orderRepository = orderRepository;
    this.memberPointRepository = memberPointRepository;
  }

  @GetMapping("/coupons")
  public ResponseEntity<List<MemberCouponInfo>> getCoupons() {
    List<MemberCouponInfo> coupons = Lists.newArrayList();
    List<MemberCoupon> memberCoupons = couponService.findMemberCouponsByMember(memberService.currentMember());
    log.debug("coupons: {}", memberCoupons);

    if (!CollectionUtils.isEmpty(memberCoupons)) {
      memberCoupons
         .stream().forEach(memberCoupon -> coupons.add(new MemberCouponInfo(memberCoupon)));
    }

    return new ResponseEntity<>(coupons, HttpStatus.OK);
  }

  @GetMapping("/shoppings")
  public ResponseEntity<ShoppingInfo> getShopping() {
    Member member = memberService.currentMember();
    if (member == null) {
      throw new MemberNotFoundException("Login required");
    }

    Date now = new Date();
    Date weekAgo = getWeekAgo(now);

    int couponCount = couponService.countByCoupons(member);
    int expectedPoint = pointService.getExpectedPoint(member);

    PointInfo pointInfo = new PointInfo(member.getPoint(), expectedPoint);
    log.debug("point into: {}", pointInfo);

    List<Order> orders = orderRepository.findByCreatedByIdAndCreatedAtBetween(member.getId(), weekAgo, now);
    if (CollectionUtils.isEmpty(orders)) {
      return new ResponseEntity<>(new ShoppingInfo(member, couponCount, pointInfo), HttpStatus.OK);
    }

    OrderCountInfo countInfo = createOrderCountByStatus(orders);
    log.debug("count info: {}", countInfo);
    return new ResponseEntity<>(new ShoppingInfo(member, couponCount, pointInfo, countInfo), HttpStatus.OK);
  }

  @GetMapping("/points")
  public CursorResponse getPoints(@RequestParam(defaultValue = "50") int count,
                                                   @RequestParam(required = false) Long cursor) {
    Member me = memberService.currentMember();
    PageRequest page = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));

    Date createdAt = null;
    if (cursor != null) {
      createdAt = new Date(cursor);
    } else {
      createdAt = new Date();
    }

    Slice<MemberPoint> points = memberPointRepository.findByMemberAndCreatedAtBefore(me, createdAt, page);
    List<PointDetailInfo> details = Lists.newArrayList();

    if (points != null) {
      points.stream().forEach(point -> {
        details.add(new PointDetailInfo(point));
      });
    }

    String nextCursor = null;
    if (!CollectionUtils.isEmpty(details)) {
      nextCursor = String.valueOf(details.get(details.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/members/me/points", details)
       .withCount(count)
       .withCursor(nextCursor).toBuild();
  }

  private OrderCountInfo createOrderCountByStatus(List<Order> orders) {
    OrderCountInfo countInfo = new OrderCountInfo();
    log.debug("count info: {}", countInfo);

    if (CollectionUtils.isEmpty(orders)) {
      return countInfo;
    }

    orders.stream().forEach(o -> {
      switch (o.getStatus()) {
        case Order.ORDER: {
          countInfo.addOrderedCount();
          break;
        }
        case Order.DELIVERED: {
          countInfo.addDeliveredCount();
          break;
        }
        case Order.DELIVERING: {
          countInfo.addDeliveringCount();
          break;
        }
        case Order.PAID: {
          countInfo.addPaidCount();
          break;
        }
        case Order.PREPARING: {
          countInfo.addPreparingCount();
          break;
        }
      }
    });

    return countInfo;
  }

  public Date getWeekAgo(Date now) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(now);
    calendar.add(Calendar.DATE, -7);
    Date monthAgo = calendar.getTime();

    log.debug("now:{}, a week ago : {}", dateFormat.format(now), dateFormat.format(monthAgo));
    return monthAgo;
  }

  @RequestMapping
  @Data
  public static class ShoppingInfo {
    private MemberInfo member;
    private int couponCount;
    private PointInfo point;
    private OrderCountInfo orderCounts;

    public ShoppingInfo(Member member, int couponCount, PointInfo point) {
      this.member = new MemberMeInfo(member);
      this.couponCount = couponCount;
      this.point = point;
    }

    public ShoppingInfo(Member member, int couponCount, PointInfo point, OrderCountInfo orderCounts) {
      this(member, couponCount, point);
      this.orderCounts = orderCounts;
    }
  }

  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  public static class OrderCountInfo {
    private int orderedCount;
    private int paidCount;
    private int preparingCount;
    private int deliveringCount;
    private int deliveredCount;

    private void addOrderedCount() {
      orderedCount = orderedCount + 1;
    }

    private void addPaidCount() {
      paidCount = paidCount + 1;
    }

    private void addPreparingCount() {
      preparingCount = preparingCount + 1;
    }

    private void addDeliveringCount() {
      deliveringCount = deliveringCount + 1;
    }

    private void addDeliveredCount() {
      deliveredCount = deliveredCount + 1;
    }
  }

  @Data
  public static class MemberCouponInfo {
    private Long id;
    private CouponInfo coupon;
    private Date createdAt;

    public MemberCouponInfo(MemberCoupon memberCoupon) {
      BeanUtils.copyProperties(memberCoupon, this);
      this.coupon = new CouponInfo(memberCoupon.getCoupon());
    }
  }

  @Data
  public static class CouponInfo {
    private int category;
    private String title;
    private String description;
    private String condition;
    private int discountPrice;
    private int discountRate;
    private int conditionPrice;
    private Date startedAt;
    private Date endedAt;

    public CouponInfo(Coupon coupon) {
      BeanUtils.copyProperties(coupon, this);
    }
  }

  @AllArgsConstructor
  @NoArgsConstructor
  @Data
  public static class PointInfo {
    private int earnedPoint;
    private int expectedPoints;
  }

  @NoArgsConstructor
  @Data
  public static class PointDetailInfo {
    private Long id;
    private int state;
    private int point;
    private Date createdAt;
    private Date earnedAt;
    private Date expiredAt;

    public PointDetailInfo(MemberPoint memberPoint) {
      BeanUtils.copyProperties(memberPoint, this);
    }
  }
}
