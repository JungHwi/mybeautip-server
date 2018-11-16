package com.jocoos.mybeautip.member.order;

import com.google.common.base.Strings;
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

  @Deprecated
  public static class Status {
    public static final String ORDERED = "ordered";
    public static final String PAID = "paid";
    public static final String PREPARING = "preparing";
    public static final String DELIVERING = "delivering";
    public static final String DELIVERED = "delivered";

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
    CONFIRMED(6), // TODO: implement order confirmed
    ORDER_CANCELLING(11),
    ORDER_CANCELLED(12),
    PAYMENT_CANCELLING(21),
    PAYMENT_CANCELLED(22),
    PAYMENT_FAIILED(23),
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
      if (Strings.isNullOrEmpty(name)) {
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

  /**
   * Order number from Godomall
   */
  @Column(nullable = false)
  private String number;

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

  @Column
  private Long videoId;

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
}
