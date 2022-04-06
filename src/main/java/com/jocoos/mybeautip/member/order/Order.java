package com.jocoos.mybeautip.member.order;

import org.apache.commons.lang3.StringUtils;
import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.member.coupon.MemberCoupon;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.util.CollectionUtils;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "orders")
public class Order extends MemberAuditable {

  /**
   * Sharing status value with Purchase status
   */

  public static class Status {
    public static final String ORDERED = "ordered";
    public static final String PAID = "paid";
    public static final String PREPARING = "preparing";
    public static final String DELIVERING = "delivering";
    public static final String DELIVERED = "delivered";
    
    public static final String CONFIRMED = "confirmed";

    public static final String PAYMENT_CANCELLING = "payment_cancelling";
    public static final String PAYMENT_CANCELLED = "payment_cancelled";
    public static final String PAYMENT_FAILED = "payment_failed";

    public static final String ORDER_CANCELLING = "order_cancelling";
    public static final String ORDER_CANCELLED = "order_cancelled";

    /**
     * Don't use in Order. Use only purchase status.
     */
    public static final String ORDER_EXCHANGING = "order_exchanging";
    public static final String ORDER_EXCHANGED = "order_exchanged";
    public static final String ORDER_RETURNING = "order_returning";
    public static final String ORDER_RETURNED = "order_returned";
  }

  public enum State {
    ORDERED(1),
    PAID(2),
    PREPARING(3),
    DELIVERING(4),
    DELIVERED(5),
    CONFIRMED(6),
    ORDER_CANCELLING(11),
    ORDER_CANCELLED(12),
    PAYMENT_CANCELLING(21),
    PAYMENT_CANCELLED(22),
    PAYMENT_FAILED(23),
    PURCHASE_EXCHANGING(41),
    PURCHASE_EXCHANGED(42),
    PURCHASE_RETURNING(43),
    PURCHASE_RETURNED(44),
    READY(99);

    private int value;

    State(int value) {
      this.value = value;
    }

    public int getValue() {
      return value;
    }

    public static State getState(String name) {
      if (StringUtils.isBlank(name)) {
        throw new IllegalArgumentException("name is required");
      }

      switch (name) {
        case Status.ORDER_EXCHANGING:
          return PURCHASE_EXCHANGING;
        case Status.ORDER_EXCHANGED:
          return PURCHASE_EXCHANGED;
        case Status.ORDER_RETURNING:
          return PURCHASE_RETURNING;
        case Status.ORDER_RETURNED:
          return PURCHASE_RETURNED;
        default:
          return State.valueOf(name.toUpperCase());
      }
    }
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(nullable = false)
  private String number;
  
  /**
   * Order number from Godomall
   */
  @Column(nullable = false)
  private String mallOrderId;

  @Column(nullable = false)
  private int goodsCount;

  @Column(nullable = false)
  private Long price;

  @Column
  private int point;

  /**
   * Payment method. ex) card, bank transfer..
   */
  @Column(nullable = false)
  private String method;
  
  @Column
  private int priceAmount;
  
  @Column
  private int deductionAmount;
  
  @Column
  private int shippingAmount;
  
  @Column
  private int expectedPoint;

  @Column(nullable = false)
  private String status;

  @Column(nullable = false)
  private int state;
  
  @Column(nullable = false)
  private String buyerPhoneNumber;
  
  @Column
  private Long videoId;
  
  @Column
  private Boolean onLive;

  @ManyToOne
  @JoinColumn(name = "coupon_id")
  private MemberCoupon memberCoupon;

  @OneToOne(mappedBy = "order")
  private Payment payment;
  
  @OneToOne(mappedBy = "order")
  private Delivery delivery;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "orderId")
  private List<Purchase> purchases;

  @Column
  @LastModifiedDate
  private Date modifiedAt;

  @Column
  private Date deletedAt;

  @Column
  private Date deliveredAt;
  
  @Transient
  private String inquiryInfo = "주문취소 신청 기간\n" +
      "주문 취소는 배송 시작 전에만 가능하며 개별의 상품의 주문 취소는 불가능 합니다. 배송 시작 후에는 교환/반품을 이용해주세요.\n\n" +
      "교환/반품 신청 기간\n" +
      "상품 수령 후 7일 이내로 고객센터 교환 및 반품 신청 (교환/반품 절차가 스토어 별로 상이하오니, 별도의 고객센터 안내에 따라 제품을 반송 해주시면 됩니다.)\n\n" +
      "교환 가능 옵션\n" +
      "교환 시 해당 제품, 다른 옵션으로만 교환 가능합니다.\n\n" +
      "단순변심에 의한 환불\n" +
      "왕복배송비는 고객부담으로 스토어 배송 정책, 배송형태에 따라 비용과 지불방식이 달라질 수 있습니다. 반송이 완료되어야 환불이 진행되며 해당 상품의 재고가 없는 경우 환불처리 될 수 있습니다.\n\n" +
      "상품 파손 및 오배송에 의한 교환/환불\n" +
      "판매자 부담이므로 상품만 보내주시면 됩니다.\n\n" +
      "교환/반품 불가 사유\n" +
      "단순변심으로 인한 교환/반품 요청이 상품을 수령한 날로부터 7일을 경과한 경우\n" +
      "포장을 개봉하여 사용 또는 일부 소비에 의하여 상품 등의 가치가 현저히 감소된 경우\n" +
      "상품을 개봉하여 사용/장착으로 상품의 가치가 훼손 된 경우\n" +
      "고객님의 책임이 있는 사유로 상품등의 가치가 심하게 파손되거나 훼손된 경우\n" +
      "구매하신 상품의 구성품(사은품 포함)이 누락된 경우\n" +
      "전자상거래 등에서 소비자보호에 관한 법률이 정하는 청약철회 제한 사유에 해당되는 경우";
  
  public Order() {
    setState(State.READY);
  }

  public void setStatus(String status) {
    State state = State.getState(status);
    if (state == null) {
      throw new IllegalArgumentException("unknown state name - " + status);
    }
    setState(state);
  }

  public void setState(State state) {
    this.status = state.name().toLowerCase();
    this.state = state.getValue();
  }


  public String getOrderTitle() {
    if (!CollectionUtils.isEmpty(purchases)) {
      if (purchases.size() == 1) {
        return purchases.get(0).getGoods().getGoodsNm();
      }

      if (purchases.size() > 1) {
        return String.format("%s 외 %d 건", purchases.get(0).getGoods().getGoodsNm(), purchases.size() - 1);
      }
    }
    return "";
  }

  public int getTotalGoodsAmount() {
    return priceAmount - deductionAmount;
  }

  public int getTotalShippingAmount() {
    return shippingAmount;
  }
  
  public boolean isConfirmed() {
    return State.CONFIRMED.getValue() == this.state;
  }
  
  public boolean isDelivered() {
    return Order.State.DELIVERED.getValue() == this.state;
  }
  
  public boolean isDelivering() {
    return Order.State.DELIVERING.getValue() == this.state;
  }
}
