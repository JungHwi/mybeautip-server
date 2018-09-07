package com.jocoos.mybeautip.member.coupon;

import javax.persistence.*;

import java.util.Date;

import org.springframework.data.annotation.LastModifiedDate;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.MemberAuditable;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "member_coupons")
public class MemberCoupon extends MemberAuditable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "coupon_id")
  private Coupon coupon;

  @Column
  private boolean used;

  @Column
  @LastModifiedDate
  private Date modifiedAt;
}
