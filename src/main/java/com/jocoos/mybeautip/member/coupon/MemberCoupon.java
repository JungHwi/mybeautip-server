package com.jocoos.mybeautip.member.coupon;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.member.Member;

@Slf4j
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
  private Date expiryAt;

  @Column
  private Date expiredAt;

  @Column
  private Date usedAt;

  public MemberCoupon(Member member, Coupon coupon) {
    this.member = member;
    this.coupon = coupon;
    this.expiryAt = coupon.getEndedAt();
  }

  public MemberCoupon(Member member, Coupon coupon, int usageDays) {
    this.member = member;
    this.coupon = coupon;

    Date now = new Date();

    this.createdAt = now;
    this.expiryAt = getExpireAt(now, usageDays);
  }

  private Date getExpireAt(Date date, int amount) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    calendar.add(Calendar.DAY_OF_YEAR, amount);
    return calendar.getTime();
  }
}
