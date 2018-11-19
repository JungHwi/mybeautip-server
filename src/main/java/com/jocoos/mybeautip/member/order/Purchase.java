package com.jocoos.mybeautip.member.order;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.LastModifiedDate;

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
   * @see Order status
   */
  @Column(nullable = false)
  private int state;

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
  private String carrier;

  @Column
  private String invoice;

  @Column
  private Date deletedAt;

  @LastModifiedDate
  private Date modifiedAt;

  @Column
  private Date deliveredAt;

  public Purchase(Long orderId, Goods goods) {
    this.orderId = orderId;
    this.goods = goods;
    setState(Order.State.ORDERED);
  }

  public void setStatus(String status) {
    Order.State state = Order.State.getState(status);
    if (state == null) {
      throw new IllegalArgumentException("unknown state name - " + status);
    }
    setState(state);
  }

  public void setState(Order.State state) {
    this.status = state.name().toLowerCase();
    this.state = state.getValue();
  }

  public boolean isDevlivering() {
    return Order.State.DELIVERING.getValue() == this.state;
  }

  public boolean isDevlivered() {
    return Order.State.DELIVERED.getValue() == this.state;
  }

  public String getGoodsName() {
    return goods.getGoodsNm();
  }

  public Integer getGoodsSupplierId() {
    return goods.getScmNo();
  }
}
