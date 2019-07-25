package com.jocoos.mybeautip.member.coupon;

import javax.persistence.*;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.member.Member;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "member_coupons")
public class MemberCoupon extends CreatedDateAuditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "coupon_id")
  private Coupon coupon;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;

  @Column
  private Date usedAt;

  public MemberCoupon(Member member, Coupon coupon) {
    this.member = member;
    this.coupon = coupon;
  }
}
