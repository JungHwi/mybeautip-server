package com.jocoos.mybeautip.restapi;

import com.google.common.collect.Lists;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.*;
import com.jocoos.mybeautip.member.order.*;
import com.jocoos.mybeautip.notification.MessageService;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members/me", produces = MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

  private final MemberService memberService;
  private final OrderService orderService;
  private final MessageService messageService;
  private final OrderRepository orderRepository;
  private final PaymentRepository paymentRepository;
  private final DeliveryRepository deliveryRepository;
  private final OrderInquiryRepository orderInquiryRepository;
  private final MemberBillingService memberBillingService;

  private static final String ORDER_NOT_FOUND = "order.not_found";
  private static final String ORDER_INQUIRY_NOT_FOUND = "order.inquiry_not_found";

  public OrderController(MemberService memberService,
                         OrderService orderService,
                         MessageService messageService,
                         OrderRepository orderRepository,
                         PaymentRepository paymentRepository,
                         DeliveryRepository deliveryRepository,
                         OrderInquiryRepository orderInquiryRepository,
                         MemberBillingService memberBillingService) {
    this.memberService = memberService;
    this.orderService = orderService;
    this.messageService = messageService;
    this.orderRepository = orderRepository;
    this.paymentRepository = paymentRepository;
    this.deliveryRepository = deliveryRepository;
    this.orderInquiryRepository = orderInquiryRepository;
    this.memberBillingService = memberBillingService;
  }

  @PostMapping("/orders")
  public ResponseEntity<OrderInfo> createOrder(@Valid @RequestBody CreateOrderRequest request,
                                               BindingResult bindingResult,
                                               @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {

    if (bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    Member member = memberService.currentMember();
    Order order = orderService.create(request, member, lang);
    log.debug("order: {}", order);
    if (order.getPrice() <= 0) {
      orderService.completeZeroOrder(order);
    }

    return new ResponseEntity<>(new OrderInfo(order), HttpStatus.OK);
  }

  @PostMapping("/orders2")
  public ResponseEntity<OrderInfo> createOrder2(@Valid @RequestBody CreateOrderRequest request,
                                                       BindingResult bindingResult,
                                                       @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {

    if (bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    Member member = memberService.currentMember();
    Order order = orderService.create(request, member, lang);

    if (order.getPrice() <= 0) {
      // do not need to pay money
      orderService.completeZeroOrder(order);
    } else {
      // check if billing info exists or not
      MemberBilling memberBilling = memberBillingService.getBaseBillingInfo(member.getId());
      String customerId = memberBillingService.getCustomerId(memberBilling);
      orderService.create2(order, customerId, member, lang);
    }

    log.debug("order: {}", order);
    return new ResponseEntity<>(new OrderInfo(order), HttpStatus.OK);
  }

  @GetMapping("/orders/{id:.+}")
  public ResponseEntity<OrderInfo> getOrder(@PathVariable Long id,
                                            @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
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
       .orElseThrow(() -> new NotFoundException("order_not_found", messageService.getMessage(ORDER_NOT_FOUND, lang)));
  }

  @GetMapping("/orders")
  public CursorResponse getOrders(@RequestParam(defaultValue = "20") int count,
                                  @RequestParam(defaultValue = "") String category,
                                  @RequestParam(required = false) Long cursor) {
    Long memberId = memberService.currentMemberId();
    PageRequest page = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "createdAt"));
    Slice<Order> orders;
    List<OrderInfo> result = Lists.newArrayList();

    Date createdAt;
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
        orders = orderRepository.findByCreatedByIdAndStateLessThanEqualAndCreatedAtBefore(
            memberId, Order.State.CONFIRMED.getValue(), createdAt, page);
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
                                                        BindingResult bindingResult,
                                                        @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    log.debug("inquiry request: {}", request);
    if (bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    Long me = memberService.currentMemberId();
    Order order = orderRepository.findByIdAndCreatedById(id, me)
        .orElseThrow(() -> new NotFoundException("order_not_found", messageService.getMessage(ORDER_NOT_FOUND, lang)));
    OrderInquiry inquiry;

    Byte state = Byte.parseByte(request.getState());
    switch (state) {
     case 0:
       inquiry = orderService.cancelOrderInquire(order, state, request.getReason());
       break;
     case 1:
     case 2: {
       if (request.getPurchaseId() == null) {
         throw new NotFoundException("purchase_not_found", "purchase id required");
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
    PageRequest page = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "createdAt"));
    Slice<OrderInquiry> inquiries;
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
        throw new NotFoundException("category_not_found", "invalid category name");
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
  public ResponseEntity<OrderInquiryInfo> getInquiry(@PathVariable Long id,
                                                     @RequestHeader(value="Accept-Language", defaultValue = "ko") String lang) {
    return orderInquiryRepository.findByIdAndCreatedById(id, memberService.currentMemberId())
       .map(orderInquiry -> new ResponseEntity<>(new OrderInquiryInfo(orderInquiry), HttpStatus.OK))
       .orElseThrow(() -> new NotFoundException("inquiry_not_found", messageService.getMessage(ORDER_INQUIRY_NOT_FOUND, lang)));
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
    @Min(0)
    private int priceAmount;
    @Min(0)
    private int deductionAmount;
    @Min(0)
    private int shippingAmount;
    @Min(0)
    private int expectedPoint;
    private Long videoId;
    private Boolean onLive;
    private Long couponId;
    @Min(0)
    private int point;
    
    @Size(max = 20)
    private String buyerPhoneNumber = "";
    
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
    private Integer goodsPrice;
    @NotNull
    private Long optionId;
    @NotNull
    private String optionValue;
    @NotNull
    private int quantity;
    private Long videoId; // FIXME: not yet used, videoId refer order's videoId
    private Boolean onLive; // FIXME: not yet used, onLive refer order's onLive
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
    private Boolean onLive;
    private DeliveryInfo delivery;
    private PaymentInfo payment;
    private List<PurchaseInfo> purchases;
    private String deliveryInfo;
    private String inquiryInfo;
    private String returnPolicy;
    private Date deliveredAt;
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
    private String cardName;
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
    private Integer state;  // 상태 (0: 구매가능, 1:품절, 2: 구매불가(판매 안함), 3: 노출안함, 4: 삭제됨)
    private String goodsNm;  // 상품명
    private int goodsPrice;
    private String thumbnail;
    private Long optionId;
    private String optionValue;
    private int quantity;
    private Long totalPrice;
    private Long videoId;
    private Boolean onLive;
    private Long inquireId;
    private String carrier;
    private String invoice;
    private String returnPolicy;
    private String inquiryInfo;
    private Date deliveredAt;
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
