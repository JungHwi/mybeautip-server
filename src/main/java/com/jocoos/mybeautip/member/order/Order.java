package com.jocoos.mybeautip.member.order;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.MemberAuditable;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "orders")
public class Order extends MemberAuditable {

  public static final String ORDER = "ordered";
  public static final String PAID = "paid";
  public static final String PREPARING = "preparing";
  public static final String DELIVERING = "delivering";
  public static final String DELIVERED = "delivered";

  public static final String PAYMENT_CANCELLED = "payment_cancelled";
  public static final String ORDER_CANCELLED = "order_cancelled";
  public static final String PAYMENT_FAILED = "payment_failed";
  public static final String PAYMENT_CANCELLING = "payment_cancelling";
  public static final String ORDER_CANCELLING = "payment_cancelling";

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long number;

  @Column(nullable = false)
  private int goodsCount;

  @Column(nullable = false)
  private Long price;

  @Column
  private int point;

  @Column(nullable = false)
  private String method;

  @Column(nullable = false)
  private String status;

  @Column
  private Long videoId;

  @OneToMany
  @JoinColumn(name = "orderId")
  private List<Purchase> purchases;

  @Column
  @LastModifiedDate
  private Date modifiedAt;

  @Column
  private Date deletedAt;
}
