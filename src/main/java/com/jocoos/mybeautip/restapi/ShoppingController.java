package com.jocoos.mybeautip.restapi;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.order.Order;
import com.jocoos.mybeautip.member.order.OrderRepository;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members/me/shoppings", produces = MediaType.APPLICATION_JSON_VALUE)
public class ShoppingController {

  private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  private final MemberService memberService;
  private final OrderRepository orderRepository;

  public ShoppingController(MemberService memberService,
                            OrderRepository orderRepository) {
    this.memberService = memberService;
    this.orderRepository = orderRepository;
  }

  @GetMapping
  private ResponseEntity<ShoppingInfo> getShopping() {
    Member member = memberService.currentMember();
    if (member == null) {
      throw new MemberNotFoundException("Login required");
    }

    Date now = new Date();
    Date weekAgo = getWeekAgo(now);

    // FIXME: Update after coupon implementation
    int couponCount = 0;

    // FIXME: Update after point implementation
    int point = 0;

    List<Order> orders = orderRepository.findByCreatedByIdAndCreatedAtBetween(member.getId(), weekAgo, now);
    if (CollectionUtils.isEmpty(orders)) {
      return new ResponseEntity<>(new ShoppingInfo(member, couponCount, point), HttpStatus.OK);
    }

    OrderCountInfo countInfo = createOrderCountByStatus(orders);
    log.debug("count info: {}", countInfo);
    return new ResponseEntity<>(new ShoppingInfo(member, couponCount, point, countInfo), HttpStatus.OK);
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
    private int point;
    private OrderCountInfo orderCounts;

    public ShoppingInfo(Member member, int couponCount, int point) {
      this.member = new MemberInfo(member, null);
      this.couponCount = 0;
      this.point = point;
    }

    public ShoppingInfo(Member member, int couponCount, int point, OrderCountInfo orderCounts) {
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
}
