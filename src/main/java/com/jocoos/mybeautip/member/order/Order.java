package com.jocoos.mybeautip.member.order;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
  public static final String ORDER_EXCHANGING = "order_exchanging";
  public static final String ORDER_EXCHANGED = "order_exchanged";
  public static final String ORDER_RETURNING = "order_returning";
  public static final String ORDER_RETURNED = "order_returned";

  public static final String PAYMENT_FAILED = "payment_failed";

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

  @Column
  private Long videoId;

  @ManyToOne
  @JoinColumn(name = "coupon_id")
  private MemberCoupon memberCoupon;


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
