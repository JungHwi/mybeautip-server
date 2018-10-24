package com.jocoos.mybeautip.restapi;

import javax.validation.Valid;
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
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.order.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members/me", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

  private final MemberService memberService;
  private final OrderService orderService;
  private final OrderRepository orderRepository;
  private final PaymentRepository paymentRepository;
  private final DeliveryRepository deliveryRepository;
  private final OrderInquiryRepository orderInquiryRepository;

  public OrderController(MemberService memberService,
                         OrderService orderService,
                         OrderRepository orderRepository,
                         PaymentRepository paymentRepository,
                         DeliveryRepository deliveryRepository,
                         OrderInquiryRepository orderInquiryRepository) {
    this.memberService = memberService;
    this.orderService = orderService;
    this.orderRepository = orderRepository;
    this.paymentRepository = paymentRepository;
    this.deliveryRepository = deliveryRepository;
    this.orderInquiryRepository = orderInquiryRepository;
  }

  @PostMapping("/orders")
  public ResponseEntity<OrderInfo> createOrder(@RequestBody CreateOrderRequest request,
                                               BindingResult bindingResult) {

    if (bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    Member member = memberService.currentMember();
    Order order = orderService.create(request, member);
    log.debug("order: {}", order);

    return new ResponseEntity<>(new OrderInfo(order), HttpStatus.OK);
  }

  @GetMapping("/orders/{id:.+}")
  public ResponseEntity<OrderInfo> getOrder(@PathVariable Long id) {
    Long memberId = memberService.currentMemberId();
    return orderRepository.findByIdAndCreatedById(id, memberId)
       .map(order -> {
         Payment payment = paymentRepository.findById(order.getId())
            .orElse(null);
         Delivery delivery = deliveryRepository.findById(order.getId()).orElse(null);

         List<PurchaseInfo> purchaseInfos = Lists.newArrayList();
         order.getPurchases().stream().forEach(p -> {
           PurchaseInfo purchaseInfo = orderInquiryRepository.findByPurchaseId(p.getId()).map(inquiry ->
              new PurchaseInfo(p, inquiry.getId())
           ).orElseGet(() -> new PurchaseInfo(p));
           purchaseInfos.add(purchaseInfo);
         });

         return new ResponseEntity<>(new OrderInfo(order, delivery, payment, purchaseInfos), HttpStatus.OK);
       })
       .orElseThrow(() -> new NotFoundException("order_not_found", "invalid order id"));
  }

  @GetMapping("/orders")
  public CursorResponse getOrders(@RequestParam(defaultValue = "20") int count,
                                  @RequestParam(defaultValue = "") String category,
                                  @RequestParam(required = false) Long cursor) {
    Long memberId = memberService.currentMemberId();
    PageRequest page = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
    Slice<Order> orders = null;
    List<OrderInfo> result = Lists.newArrayList();

    Date createdAt = null;
    if (cursor != null) {
      createdAt = new Date(cursor);
    } else {
      createdAt = new Date();
    }

    switch (category) {
      case "delivery": {
        orders = orderRepository.findByCreatedByIdAndCreatedAtBeforeAndStatusContains(memberId, createdAt, "deliver", page);
        break;
      }
      default: {
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

  @PostMapping("/orders/{id:.+}/inquiries")
  public ResponseEntity<OrderInquiryInfo> createInquiry(@PathVariable Long id,
                                                        @Valid @RequestBody CreateOrderInquiry request,
                                                        BindingResult bindingResult) {
    log.debug("inquiry request: {}", request);
    if (bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    Long me = memberService.currentMemberId();
    Order order = orderRepository.findByIdAndCreatedById(id, me).orElseThrow(() -> new NotFoundException("order_not_found", "invalid order id"));
    OrderInquiry inquiry = null;

    Byte state = Byte.parseByte(request.getState());
    switch (state) {
     case 0:
       inquiry = orderService.cancelOrderInquire(order, state, request.getReason());
       break;
     case 1:
     case 2: {
       if (request.getPurchaseId() == null) {
         throw new BadRequestException("purchase_id_not_found", "purchase id required");
       }
       Purchase purchase = order.getPurchases().stream().filter(p -> p.getId().equals(request.getPurchaseId())).findAny().orElseThrow(() -> new NotFoundException("purchase_not_found", "invalid purchase id"));
       inquiry = orderService.inquiryExchangeOrReturn(order, Byte.parseByte(request.getState()), request.getReason(), purchase);
       break;
     }
     default:
       throw new IllegalArgumentException("Unknown state type");
    }

    return new ResponseEntity<>(new OrderInquiryInfo(inquiry), HttpStatus.OK);
  }

  @GetMapping("/inquiries")
  public CursorResponse getInquires(@RequestParam String category,
                                    @RequestParam(defaultValue = "20") int count,
                                    @RequestParam(required = false) Long cursor) {

    Long me = memberService.currentMemberId();
    PageRequest page = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
    Slice<OrderInquiry> inquiries = null;
    switch (category) {
      case "cancel": {
        if (cursor != null) {
          inquiries = orderInquiryRepository.findByStateAndCreatedAtBeforeAndCreatedById(OrderInquiry.STATE_CANCEL_ORDER, new Date(cursor), me, page);
        } else {
          inquiries = orderInquiryRepository.findByStateAndCreatedById(OrderInquiry.STATE_CANCEL_ORDER, me, page);
        }
        break;
      }
      case "exchange": {
        if (cursor != null) {
          inquiries = orderInquiryRepository.findByStateGreaterThanEqualAndCreatedAtBeforeAndCreatedById(OrderInquiry.STATE_REQUEST_EXCHANGE, new Date(cursor), me, page);
        } else {
          inquiries = orderInquiryRepository.findByCreatedByIdAndStateGreaterThanEqual(me, OrderInquiry.STATE_REQUEST_EXCHANGE, page);
        }
        break;
      }
      default: {
        throw new BadRequestException("category_not_found", "invalid category name");
      }
    }

    List<OrderInquiryInfo> result = Lists.newArrayList();
    if (inquiries != null && inquiries.getSize() > 0) {
      inquiries.stream().forEach(inquiry -> result.add(new OrderInquiryInfo(inquiry)));
    }

    String nextCursor = null;
    if (!CollectionUtils.isEmpty(result)) {
      nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
    }

    return new CursorResponse.Builder<>("/api/1/members/me/inquiries", result)
       .withCount(count)
       .withCursor(nextCursor)
       .withCategory(category)
       .toBuild();
  }

  @GetMapping("/inquiries/{id:.+}")
  public ResponseEntity<OrderInquiryInfo> getInquiry(@PathVariable Long id) {
    return orderInquiryRepository.findByIdAndCreatedById(id, memberService.currentMemberId())
       .map(orderInquiry -> new ResponseEntity<>(new OrderInquiryInfo(orderInquiry), HttpStatus.OK))
       .orElseThrow(() -> new NotFoundException("inquiry_not_found", "invalid inquiry id"));
  }

  @Data
  public static class CreateOrderInquiry {
    @NotNull
    private String state;
    @NotNull
    private String reason;

    private Long purchaseId;
  }

  @Data
  public static class CreateOrderRequest {
    @NotNull
    private Long price;
    private int point;
    @NotNull
    private String method;
    private int priceAmount;
    private int deductionAmount;
    private int shippingAmount;
    private int expectedPoint;
    private Long videoId;
    private Long couponId;
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
    private int priceAmount;
    private int deductionAmount;
    private int shippingAmount;
    private int expectedPoint;
    private int state;
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

      if (order.getPayment() != null) {
        this.payment = new PaymentInfo(order.getPayment());
      }
  
      if (order.getDelivery() != null) {
        this.delivery = new DeliveryInfo(order.getDelivery());
      }
    }

    public OrderInfo(Order order, Delivery delivery, Payment payment, List<PurchaseInfo> purchases) {
      BeanUtils.copyProperties(order, this);
      this.delivery = new DeliveryInfo(delivery);
      this.payment = new PaymentInfo(payment);
      this.purchases = purchases;
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
    private Long orderId;
    private String status;
    private String goodsNo;
    private String goodsNm;  // 상품명
    private int goodsPrice;
    private String thumbnail;
    private Long optionId;
    private String optionValue;
    private String optionPrice;
    private int quantity;
    private Long totalPrice;
    private Long videoId;
    private Long inquireId;
    private Date createdAt;


    public PurchaseInfo(Purchase purchase) {
      BeanUtils.copyProperties(purchase, this);
      this.thumbnail = purchase.getGoods().getListImageData().toString();
      this.goodsNo = purchase.getGoods().getGoodsNo();
      this.goodsNm = purchase.getGoods().getGoodsNm();
    }

    public PurchaseInfo(Purchase purchase, Long inquireId) {
      this(purchase);
      this.inquireId = inquireId;
    }
  }

  @NoArgsConstructor
  @Data
  public static class OrderInquiryInfo {
    private Long id;
    private Byte state;
    private String reason;
    private String comment;
    private OrderInfo order;
    private PurchaseInfo purchase;
    private boolean completed;
    private Date createdAt;
    private Date modifiedAt;

    public OrderInquiryInfo(OrderInquiry orderInquiry) {
      BeanUtils.copyProperties(orderInquiry, this);
      this.order = new OrderInfo(orderInquiry.getOrder());
      if (orderInquiry.getPurchase() != null) {
        this.purchase = new PurchaseInfo(orderInquiry.getPurchase());
      }
    }
  }
}
