package com.jocoos.mybeautip.member.order;

import javax.persistence.*;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.goods.Goods;

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

  @ManyToOne
  @JoinColumn(name = "goods_no")
  private Goods goods;

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

  public Purchase(Long orderId, String status, Goods goods) {
    this.orderId = orderId;
    this.status = status;
    this.goods = goods;
  }
}
