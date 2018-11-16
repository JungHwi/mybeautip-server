package com.jocoos.mybeautip.goods;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
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

  public DeliveryCharge(int id) {
    this.id = id;
  }
}