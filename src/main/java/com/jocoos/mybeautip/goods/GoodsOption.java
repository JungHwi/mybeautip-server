package com.jocoos.mybeautip.goods;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "goods_options")
public class GoodsOption {
  @Id
  Integer sno;
  Integer optionNo;
  Integer goodsNo;
  String optionValue1;
  String optionValue2;
  String optionValue3;
  String optionValue4;
  String optionValue5;
  Integer optionPrice;
  Integer optionCostPrice;
  String optionViewFl;
  String optionSellFl;
  String optionCode;
  Integer stockCnt;
  String optionMemo;
  String optionImage;
}
