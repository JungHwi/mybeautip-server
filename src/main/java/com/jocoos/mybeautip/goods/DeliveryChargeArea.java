package com.jocoos.mybeautip.goods;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "delivery_charge_area")
public class DeliveryChargeArea {
  @Id
  private Integer id;

  @Column(nullable = false)
  private String area;

  @Column(nullable = false)
  private String part1;

  @Column(nullable = false)
  private String part2;

  @Column(nullable = false)
  private String part3;

  @Column(nullable = false)
  private String part4;

  @Column(nullable = false)
  private Integer price;
}
