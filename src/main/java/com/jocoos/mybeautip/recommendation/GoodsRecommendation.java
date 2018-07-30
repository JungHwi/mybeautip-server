package com.jocoos.mybeautip.recommendation;

import com.jocoos.mybeautip.goods.Goods;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "goods_recommendations")
public class GoodsRecommendation {

  @Id
  @Column(name = "goods_no")
  private String goodsNo;

  @MapsId
  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(
      name = "goods_no",
      referencedColumnName = "goods_no",
      foreignKey = @ForeignKey(name = "fk_goods_recommendations_goods")
  )
  private Goods goods;

  @Column(nullable = false)
  private int seq;

  @Column(nullable = false)
  @CreatedBy
  private Long createdBy;

  @Column(nullable = false)
  @CreatedDate
  private Date createdAt;

  @Column
  @LastModifiedDate
  private Date modifiedAt;

  @Column
  private Date startedAt;

  @Column
  private Date endedAt;
}