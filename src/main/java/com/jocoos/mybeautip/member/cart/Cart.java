package com.jocoos.mybeautip.member.cart;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.MemberAuditable;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.goods.GoodsOption;
import com.jocoos.mybeautip.store.Store;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "carts")
public class Cart extends MemberAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Boolean checked;

  @ManyToOne
  @JoinColumn(name = "goods_no")
  private Goods goods;

  @ManyToOne
  @JoinColumn(name = "option_id")
  private GoodsOption option;

  @ManyToOne
  @JoinColumn(name = "store_id")
  private Store store;

  @Column(nullable = false)
  private Integer quantity;

  @Column
  private Date modifiedAt;

  public Cart(Goods goods, GoodsOption option, Store store, int quantity) {
    this.checked = true;
    this.goods = goods;
    this.option = option;
    this.store = store;
    this.quantity = quantity;
  }
}