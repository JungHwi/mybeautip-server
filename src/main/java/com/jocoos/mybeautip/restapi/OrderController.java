package com.jocoos.mybeautip.restapi;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.Lists;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.order.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members/me/orders", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

  private final MemberService memberService;
  private final OrderService orderService;
  private final OrderRepository orderRepository;
  private final PaymentRepository paymentRepository;
  private final DeliveryRepository deliveryRepository;

  public OrderController(MemberService memberService,
                         OrderService orderService,
                         OrderRepository orderRepository,
                         PaymentRepository paymentRepository,
                         DeliveryRepository deliveryRepository) {
    this.memberService = memberService;
    this.orderService = orderService;
    this.orderRepository = orderRepository;
    this.paymentRepository = paymentRepository;
    this.deliveryRepository = deliveryRepository;
  }

  @PostMapping
  public ResponseEntity<OrderInfo> createOrder(@RequestBody CreateOrderRequest request,
                                               BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    Order order = orderService.create(request);
    log.debug("order: {}", order);

    return new ResponseEntity<>(new OrderInfo(order), HttpStatus.OK);
  }

  @GetMapping("/{id:.+}")
  public ResponseEntity<OrderInfo> getOrder(@PathVariable Long id) {
    Long memberId = memberService.currentMemberId();
    return orderRepository.findByIdAndCreatedById(id, memberId)
       .map(order -> {
         Payment payment = paymentRepository.findById(order.getId())
            .orElse(null);
         Delivery delivery = deliveryRepository.findById(order.getId()).orElse(null);
         return new ResponseEntity<>(new OrderInfo(order, delivery, payment), HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("order_not_found", "invalid order id"));
  }

  @GetMapping
  public CursorResponse getOrders(@RequestParam(defaultValue = "20") int count,
                                  @RequestParam(defaultValue = "delivery") String category,
                                  @RequestParam(required = false) Long cursor) {
    Long memberId = memberService.currentMemberId();
    PageRequest page = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
    Slice<Order> orders = null;
    List<OrderInfo> result = Lists.newArrayList();

    switch (category) {
      default: {
        // category=delivery
        Date createdAt = null;
        if (cursor != null) {
          createdAt = new Date(cursor);
        } else {
          createdAt = new Date();
        }
        orders = orderRepository.findByCreatedByIdAndCreatedAtBefore(memberId, createdAt, page);
      }
    }

    if (orders != null && orders.getSize() > 0) {
      orders.stream().forEach(o -> result.add(new OrderInfo(o)));
    }

    String nextCursor = null;
    if (!CollectionUtils.isEmpty(result)) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/members/me/orders", result)
       .withCount(count)
       .withCursor(nextCursor)
       .withCategory(category)
       .toBuild();
  }


  @Data
  public static class CreateOrderRequest {
    @NotNull
    private Long price;
    private int point;
    @NotNull
    private String method;
    private Long videoId;
    @NotNull
    private CreateDeliveryRequest delivery;
    @NotNull
    private CreatePaymentRequest payment;
    @NotNull
    private List<CreatePurchaseRequest> purchases;
  }

  @Data
  public static class CreateDeliveryRequest {
    @NotNull
    private String recipient;
    @NotNull
    private String phone;
    @NotNull
    private String zipNo;
    @NotNull
    private String roadAddrPart1;
    @NotNull
    private String roadAddrPart2;
    @NotNull
    private String jibunAddr;
    @NotNull
    private String detailAddress;
    private String carrierMessage;
  }

  @Data
  public static class CreatePaymentRequest {
    @NotNull
    private Long price;
    @NotNull
    private String method;
  }

  @Data
  public static class CreatePurchaseRequest {
    @NotNull
    private String goodsNo;
    @NotNull
    private int goodsPrice;
    @NotNull
    private Long optionId;
    @NotNull
    private String optionValue;
    @NotNull
    private String optionPrice;
    @NotNull
    private int quantity;
    private Long videoId;
  }

  @NoArgsConstructor
  @Data
  public static class OrderInfo {
    private Long id;
    private String number;
    private int goodsCount;
    private Long price;
    private MemberInfo createdBy;
    private int point;
    private String method;
    private int status;
    private Long videoId;
    private DeliveryInfo delivery;
    private PaymentInfo payment;
    private List<PurchaseInfo> purchases;
    private Date createdAt;
    private Date modifiedAt;

    public OrderInfo(Order order) {
      BeanUtils.copyProperties(order, this);
      log.debug("purchases: {}", purchases);

      purchases = Lists.newArrayList();

      List<Purchase> orderPurchases = order.getPurchases();
      if (!CollectionUtils.isEmpty(orderPurchases)) {
        orderPurchases.forEach(p -> purchases.add(new PurchaseInfo(p)));
      }
    }

    public OrderInfo(Order order, Delivery delivery, Payment payment) {
      this(order);
      this.delivery = new DeliveryInfo(delivery);
      this.payment = new PaymentInfo(payment);
    }
  }

  @NoArgsConstructor
  @Data
  public static class DeliveryInfo {
    private Long id;
    private String recipient;
    private String phone;
    private String zipNo;
    private String roadAddrPart1;
    private String roadAddrPart2;
    private String jibunAddr;
    private String detailAddress;
    private String carrier;
    private String invoice;
    private String carrierMessage;
    private Date createdAt;

    public DeliveryInfo(Delivery delivery) {
      BeanUtils.copyProperties(delivery, this);
    }
  }

  @NoArgsConstructor
  @Data
  public static class PaymentInfo {
    private Long id;
    private String paymentId;
    private String method;
    private int state;
    private String message;
    private String receipt;
    private Date createdAt;
    private Date modifiedAt;
    private Date deletedAt;

    public PaymentInfo(Payment payment) {
      BeanUtils.copyProperties(payment, this);
    }
  }

  @NoArgsConstructor
  @Data
  public static class PurchaseInfo {
    private Long id;
    private String goodsNo;
    private int goodsPrice;
    private Long optionId;
    private String optionValue;
    private String optionPrice;
    private int quantity;
    private Long totalPrice;
    private Long videoId;
    private Date createdAt;
    // TODO: Add goods info for thumbnail?

    public PurchaseInfo(Purchase purchase) {
      BeanUtils.copyProperties(purchase, this);
    }
  }
}
