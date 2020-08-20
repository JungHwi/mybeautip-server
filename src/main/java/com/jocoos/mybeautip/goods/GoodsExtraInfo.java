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
@Table(name = "goods_extra_info")
public class GoodsExtraInfo {
  @Id
  private Integer id;

  @Column(nullable = false)
  private String goodsNo;  // 상품번호

  private Integer state; // 1: MMS, ...

  @Column
  private String extraFeeInfo;
}
