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
  
  @Transient
  private String returnPolicy = "배송비 결제 확인 후 상품 교환이 진행 됩니다. 반품 배송비는 각 스토어의 규정을 따르고 있으며 상품 스토어에서 고객님께 별도의 안내를 드리고 있습니다.  문의사항은 해당 스토어 고객센터(010-9482-5590)로 문의해 주세요.";
  
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

  public boolean isDelivering() {
    return Order.State.DELIVERING.getValue() == this.state;
  }
  
  public boolean isConfirmed() {
    return Order.State.CONFIRMED.getValue() == this.state;
  }
  
  public boolean isDelivered() {
    return Order.State.DELIVERED.getValue() == this.state;
  }

  public String getGoodsName() {
    return goods.getGoodsNm();
  }

  public Integer getGoodsSupplierId() {
    return goods.getScmNo();
  }
}
