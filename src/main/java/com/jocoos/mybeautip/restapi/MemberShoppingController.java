package com.jocoos.mybeautip.restapi;

import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;

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
import com.jocoos.mybeautip.member.point.MemberPointService;
import com.jocoos.mybeautip.notification.MessageService;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members/me", produces = MediaType.APPLICATION_JSON_VALUE)
public class MemberShoppingController {

  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private final MemberService memberService;
  private final CouponService couponService;
  private final MemberPointService memberPointService;

  private final OrderRepository orderRepository;
  private final MemberPointRepository memberPointRepository;
  private final MessageService messageService;

  public MemberShoppingController(MemberService memberService,
                                  CouponService couponService,
                                  MemberPointService memberPointService,
                                  OrderRepository orderRepository,
                                  MemberPointRepository memberPointRepository,
                                  MessageService messageService) {
    this.memberService = memberService;
    this.couponService = couponService;
    this.memberPointService = memberPointService;
    this.orderRepository = orderRepository;
    this.memberPointRepository = memberPointRepository;
    this.messageService = messageService;
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
    Date weekAgo = DateUtils.addDays(new Date(), -7);

    int couponCount = couponService.countByCoupons(member);
    int expectedPoint = memberPointService.getExpectedPoint(member);

    PointInfo pointInfo = new PointInfo(member.getPoint(), expectedPoint);
    log.debug("point into: {}", pointInfo);

    List<Order> list = orderRepository.findByCreatedByIdAndStateLessThanEqual(member.getId(), 4);
    List<Order> deliveredList = orderRepository.findByCreatedByIdAndStateAndDeliveredAtAfter(member.getId(), 5, weekAgo);
    list.addAll(deliveredList);
    if (CollectionUtils.isEmpty(list)) {
      return new ResponseEntity<>(new ShoppingInfo(member, couponCount, pointInfo), HttpStatus.OK);
    }

    List<OrderController.OrderInfo> orders = Lists.newArrayList();
    list.forEach(o -> orders.add(new OrderController.OrderInfo(o)));

    Collections.sort(orders, new OrderCreatedAtDesc());

    OrderCountInfo countInfo = createOrderCountByStatus(list);
    log.debug("count info: {}", countInfo);
    return new ResponseEntity<>(new ShoppingInfo(member, couponCount, pointInfo, countInfo, orders), HttpStatus.OK);
  }

  static class OrderCreatedAtDesc implements Comparator<OrderController.OrderInfo> {
    @Override
    public int compare(OrderController.OrderInfo o1, OrderController.OrderInfo o2) {
      return o2.getCreatedAt().compareTo(o1.getCreatedAt());
    }
  }

  @GetMapping("/points")
  public CursorResponse getPoints(@RequestParam(defaultValue = "50") int count,
                                  @RequestParam(required = false) Long cursor,
                                  @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    Member me = memberService.currentMember();
    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "createdAt"));

    Date createdAt;
    if (cursor != null) {
      createdAt = new Date(cursor);
    } else {
      createdAt = new Date();
    }

    Slice<MemberPoint> points = memberPointRepository.findByMemberAndCreatedAtBefore(me, createdAt, pageable);
    List<PointDetailInfo> details = Lists.newArrayList();

    if (points != null) {
      points.stream().forEach(point -> {
        switch (point.getState()) {
          case MemberPoint.STATE_PRESENT_POINT:
            point.setState(MemberPoint.STATE_EARNED_POINT);

            String adminMessage = messageService.getMessage("point.by_admin", lang);
            details.add(new PointDetailInfo(point, adminMessage));
            break;
          case MemberPoint.STATE_REFUNDED_POINT:
            point.setState(MemberPoint.STATE_EARNED_POINT);

            String refundMessage = messageService.getMessage("point.refunded_by_cancel", lang);
            details.add(new PointDetailInfo(point, refundMessage));
            break;
          default:
            details.add(new PointDetailInfo(point));
        }
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

    orders.forEach(o -> {
      switch (o.getStatus()) {
        case Order.Status.ORDERED: {
          countInfo.addOrderedCount();
          break;
        }
        case Order.Status.DELIVERED: {
          countInfo.addDeliveredCount();
          break;
        }
        case Order.Status.DELIVERING: {
          countInfo.addDeliveringCount();
          break;
        }
        case Order.Status.PAID: {
          countInfo.addPaidCount();
          break;
        }
        case Order.Status.PREPARING: {
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
    private List<OrderController.OrderInfo> orders;

    public ShoppingInfo(Member member, int couponCount, PointInfo point) {
      this.member = new MemberMeInfo(member);
      this.couponCount = couponCount;
      this.point = point;
    }

    public ShoppingInfo(Member member, int couponCount, PointInfo point, OrderCountInfo orderCounts, List<OrderController.OrderInfo> orders) {
      this(member, couponCount, point);
      this.orderCounts = orderCounts;
      this.orders = orders;
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
      CouponInfo couponInfo = new CouponInfo(memberCoupon.getCoupon());
      couponInfo.setStartedAt(memberCoupon.getCreatedAt());
      couponInfo.setEndedAt(memberCoupon.getExpiryAt());

      this.coupon = couponInfo;
    }
  }

  @Data
  public static class CouponInfo {
    private Long id;
    private byte category;
    private String title;
    private String description;
    private String condition;
    private int discountPrice;
    private int discountRate;
    private int conditionPrice;
    private int usePriceLimit;
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
    private String message = "";

    public PointDetailInfo(MemberPoint memberPoint) {
      BeanUtils.copyProperties(memberPoint, this);
    }

    public PointDetailInfo(MemberPoint memberPoint, String message) {
      this(memberPoint);
      this.message = message;
    }
  }
}
