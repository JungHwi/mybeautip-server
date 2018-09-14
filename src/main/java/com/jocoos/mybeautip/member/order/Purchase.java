package com.jocoos.mybeautip.member.order;

import javax.persistence.*;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "order_purchases")
public class Purchase extends CreatedDateAuditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long orderId;

  @Column(nullable = false)
  private String goodsNo;

  /**
   * @see Order#status
   */
  @Column(nullable = false)
  private String status;

  @Column(nullable = false)
  private int goodsPrice;

  @Column(nullable = false)
  private Long optionId;

  @Column(nullable = false)
  private String optionValue;

  @Column
  private String optionPrice;

  @Column(nullable = false)
  private int quantity;

  @Column(nullable = false)
  private Long totalPrice;

  @Column
  private Long videoId;

  @Column
  private Date deletedAt;

  public Purchase(Long orderId, String status) {
    this.orderId = orderId;
    this.status = status;
  }
}
