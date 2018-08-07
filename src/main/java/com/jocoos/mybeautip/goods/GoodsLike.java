package com.jocoos.mybeautip.goods;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "goods_likes")
public class GoodsLike {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @JsonIgnore
  @Column(name = "goods_no")
  private String goodsNo;

  @OneToOne(cascade = {CascadeType.ALL})
  @JoinColumn(name = "goods_no", insertable = false, updatable = false)
  private Goods goods;

  @Column(nullable = false)
  @CreatedBy
  private Long createdBy;

  @Column(nullable = false)
  @CreatedDate
  private Date createdAt;

  public GoodsLike(String goodsNo, Goods goods) {
    this.goodsNo = goodsNo;
    this.goods = goods;
  }
}