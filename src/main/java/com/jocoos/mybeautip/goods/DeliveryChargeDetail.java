package com.jocoos.mybeautip.goods;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.godo.GodoDeliveryResponse;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "delivery_charge_details")
public class DeliveryChargeDetail {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "delivery_charge_id")
  private DeliveryCharge deliveryCharge;

  @Column(nullable = false)
  private Integer unitStart;

  @Column(nullable = false)
  private Integer unitEnd;

  @Column(nullable = false)
  private Integer price;

  public DeliveryChargeDetail(DeliveryCharge deliveryCharge, GodoDeliveryResponse.ChargeData data) {
    this.deliveryCharge = deliveryCharge;
    this.unitStart = data.getUnitStart().intValue();
    this.unitEnd = data.getUnitEnd().intValue();
    this.price = data.getPrice().intValue();
  }
}
