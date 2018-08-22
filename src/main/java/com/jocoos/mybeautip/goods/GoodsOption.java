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
  Integer optionPrice;
  Integer optionCostPrice;
  Integer stockCnt;

  @Column
  @CreatedDate
  private Date createdAt;

  @Column
  @LastModifiedDate
  private Date modifiedAt;
}
