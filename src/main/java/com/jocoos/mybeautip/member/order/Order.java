package com.jocoos.mybeautip.member.order;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;
import lombok.EqualsAndHashCode;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.member.coupon.MemberCoupon;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "orders")
public class Order extends MemberAuditable {

  /**
   * Sharing status value with Purchase status
   */
  public static final String ORDER = "ordered";
  public static final String PAID = "paid";
  public static final String PREPARING = "preparing";
  public static final String DELIVERING = "delivering";
  public static final String DELIVERED = "delivered";

  public static final String PAYMENT_CANCELLING = "payment_cancelling";
  public static final String PAYMENT_CANCELLED = "payment_cancelled";
  public static final String ORDER_CANCELLING = "order_cancelling";
  public static final String ORDER_CANCELLED = "order_cancelled";

  /**
   * Don't use in Order. Use only purchase status.
   */
  public static final String ORDER_EXCHANGING = "order_exchanging";
  public static final String ORDER_EXCHANGED = "order_exchanged";
  public static final String ORDER_RETURNING = "order_returning";
  public static final String ORDER_RETURNED = "order_returned";

  public static final String PAYMENT_FAILED = "payment_failed";


  public static final int STATE_ORDER = 1;
  public static final int STATE_PAID = 2;
  public static final int STATE_PREPARING = 3;
  public static final int STATE_DELIVERING = 4;
  public static final int STATE_DELIVERED = 5;

  public static final int STATE_ORDER_CANCELLING = 11;
  public static final int STATE_ORDER_CANCELLED = 12;

  public static final int STATE_PAYMENT_CANCELLING = 21;
  public static final int STATE_PAYMENT_CANCELLED = 22;


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

  public Order() {
    status = ORDER;
  }

  public void setPurchaseStatus(Long purchaseId, String status) {
    getPurchases().stream().forEach(p -> {
      if (purchaseId.equals(p.getId())) {
        p.setStatus(status);
      }
    });
  }
}
