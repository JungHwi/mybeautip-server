package com.jocoos.mybeautip.recommendation;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.goods.Goods;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "recommended_goods")
public class GoodsRecommendation extends MemberAuditable {

  @Id
  @Column(name = "goods_no")
  private String goodsNo;

  @MapsId("goods_no")
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "goods_no")
  private Goods goods;

  @Column(nullable = false)
  private int seq;

  @Column
  @LastModifiedDate
  private Date modifiedAt;

  @Column
  private Date startedAt;

  @Column
  private Date endedAt;
}