package com.jocoos.mybeautip.video;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.goods.Goods;

@NoArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "video_goods")
public class VideoGoods {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "video_key")
  private Video video;

  @ManyToOne
  @JoinColumn(name = "goods_no")
  private Goods goods;

  @Column
  @CreatedDate
  public Date createdAt;

  public VideoGoods(Video video, Goods goods) {
    this.video = video;
    this.goods = goods;
  }
}