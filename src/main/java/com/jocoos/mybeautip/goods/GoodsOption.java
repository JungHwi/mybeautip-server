package com.jocoos.mybeautip.goods;

import javax.persistence.*;

import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
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

  @Column
  @CreatedDate
  private Date createdAt;

  @Column
  @LastModifiedDate
  private Date modifiedAt;
}
