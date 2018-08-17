package com.jocoos.mybeautip.member.cart;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.goods.Goods;

@NoArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "carts")
public class Cart {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "goods_no")
  private Goods goods;

  @Column(nullable = false)
  private Integer optionNo;

  @Column(nullable = false)
  private Integer scmNo;

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false)
  @CreatedBy
  private Long createdBy;

  @Column(nullable = false)
  @CreatedDate
  private Date createdAt;

  @Column
  @LastModifiedDate
  private Date modifiedAt;


  public Cart(Goods goods, int optionNo, int quantity) {
    this.goods = goods;
    this.optionNo = optionNo;
    this.scmNo = goods.getScmNo();
    this.quantity = quantity;
  }
}