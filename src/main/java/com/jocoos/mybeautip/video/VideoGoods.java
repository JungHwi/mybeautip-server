package com.jocoos.mybeautip.video;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.goods.Goods;
import com.jocoos.mybeautip.member.Member;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Data
@NoArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "video_goods")
public class VideoGoods extends CreatedDateAuditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String videoKey;

  @Column(name = "type")
  private String videoType;

  private String thumbnailUrl;

  @JsonIgnore
  @Column(name = "member_id")
  private Long memberId;

  @OneToOne(cascade = {CascadeType.ALL})
  @JoinColumn(name = "member_id", insertable = false, updatable = false)
  private Member member;

  @JsonIgnore
  @Column(name = "goods_no")
  private String goodsNo;

  @OneToOne(cascade = {CascadeType.ALL})
  @JoinColumn(name = "goods_no", insertable = false, updatable = false)
  private Goods goods;

  @JsonIgnore
  public Date getCreatedAt() {
    return createdAt;
  }

  public VideoGoods(String videoKey, String goodsNo, long me,
                    String videoType, String thumbnailUrl) {
    this.videoKey = videoKey;
    this.goodsNo = goodsNo;
    this.memberId = me;
    this.videoType = videoType;
    this.thumbnailUrl = thumbnailUrl;
  }
}