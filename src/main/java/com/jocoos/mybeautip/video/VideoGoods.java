package com.jocoos.mybeautip.video;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.member.Member;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "video_goods")
public class VideoGoods {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "video_id")
  private Video video;

  @ManyToOne
  @JoinColumn(name = "goods_no")
  private Goods goods;

  @ManyToOne
  @JoinColumn(name = "member_id")
  protected Member member;

  @Column
  @CreatedDate
  protected Date createdAt;

  public VideoGoods(Video video, Goods goods, Member member) {
    this.video = video;
    this.goods = goods;
    this.member = member;
  }
}