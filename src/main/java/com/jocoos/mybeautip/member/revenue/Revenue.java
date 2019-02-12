package com.jocoos.mybeautip.member.revenue;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.member.order.Purchase;
import com.jocoos.mybeautip.video.Video;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@Data
@Entity
@Table(name = "member_revenues")
public class Revenue extends CreatedDateAuditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "video_id")
  private Video video;

  @ManyToOne
  @JoinColumn(name = "purchase_id")
  private Purchase purchase;

  @Column
  private int revenue;
  
  @Column
  private Date confirmedAt;
  
  @ManyToOne
  @JoinColumn(name = "revenue_payment_id")
  private RevenuePayment revenuePayment;

  public Revenue(Video video, Purchase purchase, int revenue, RevenuePayment revenuePayment) {
    this.video = video;
    this.purchase = purchase;
    this.revenue = revenue;
    this.revenuePayment = revenuePayment;
  }
}
