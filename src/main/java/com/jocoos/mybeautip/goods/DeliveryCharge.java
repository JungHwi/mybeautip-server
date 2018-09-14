package com.jocoos.mybeautip.goods;

import javax.persistence.*;

import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "delivery_charge")
public class DeliveryCharge {
  @Id
  private Integer id;

  @Column(nullable = false)
  private Integer scmNo;

  @Column(nullable = false)
  private String method;

  @Column
  private String description;

  @Column(nullable = false)
  private String collectFl;

  @Column(nullable = false)
  private String fixFl;

  @Column
  private String chargeData;

  public DeliveryCharge(int id) {
    this.id = id;
  }
}