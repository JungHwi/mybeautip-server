package com.jocoos.mybeautip.member.coupon;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.MemberAuditable;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "coupons")
public class Coupon extends MemberAuditable {

  public static Byte CATEGORY_WELCOME_COUPON = Byte.parseByte("1");

  public static int DISCOUNT_FIXED_PRICE = 1;
  public static int DISCOUNT_FIXED_RATE = 2;
  public static int DISCOUNT_DELIVERY_FEE = 3;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Coupon category. 0: welcome, 1: fixed price
   */
  @Column
  private Byte category;

  @Column(nullable = false, length = 20)
  private String title;

  @Column(nullable = false, length = 128)
  private String description;

  @Column(nullable = false, length = 128)
  private String condition;

  /**
   * Discount by fixed price. ex) 30000
   */
  @Column
  private int discountPrice;

  /**
   * Discount by rate. ex) 10?
   */
  @Column
  private int discountRate;
  
  /**
   * Minimum condition for discount. ex) 50000
   */
  @Column
  private int conditionPrice;
  
  /**
   * Upper bound of discount. ex) 5만원 이상 구매시 사용 (최대 1만원까지)
   */
  @Column
  private int usePriceLimit;
  
  @Column(nullable = false)
  private Date startedAt;

  @Column(nullable = false)
  private Date endedAt;

  @Column
  @LastModifiedDate
  private Date modifiedAt;

  @Column
  private Date deletedAt;
}
