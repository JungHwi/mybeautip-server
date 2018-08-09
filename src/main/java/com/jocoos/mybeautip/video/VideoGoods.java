package com.jocoos.mybeautip.video;

import javax.persistence.*;
import java.util.Date;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;

import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.member.Member;

@NoArgsConstructor
@Data
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "video_goods")
public class VideoGoods {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String videoKey;

  @Column(name = "type")
  private String videoType;

  private String thumbnailUrl;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;

  @ManyToOne
  @JoinColumn(name = "goods_no")
  private Goods goods;

  @JsonIgnore
  @Column
  @CreatedDate
  public Date createdAt;

  public VideoGoods(String videoKey, String videoType, String thumbnailUrl,
                    Goods goods, Member me) {
    this.videoKey = videoKey;
    this.videoType = videoType;
    this.thumbnailUrl = thumbnailUrl;
    this.goods = goods;
    this.member = me;
  }
}