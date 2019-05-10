package com.jocoos.mybeautip.member.point;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.audit.CreatedDateAuditable;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.order.Order;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "member_points")
public class MemberPoint extends CreatedDateAuditable {

  public static final int STATE_WILL_BE_EARNED = 0;
  public static final int STATE_EARNED_POINT = 1;
  public static final int STATE_USE_POINT = 2;
  public static final int STATE_EXPIRED_POINT = 3;
  public static final int STATE_PRESENT_POINT = 9;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private int state;

  @Column(nullable = false)
  private int point;

  @ManyToOne
  @JoinColumn(name = "member_id")
  private Member member;
  
  @ManyToOne
  @JoinColumn(name = "order_id")
  private Order order;

  @Column
  private Date earnedAt;

  @Column
  private Date expiredAt;

  public MemberPoint(Member member, Order order, int point) {
    this(member, order, point, STATE_WILL_BE_EARNED);
  }

  public MemberPoint(Member member, Order order, int point, int state) {
    this.member = member;
    this.order = order;
    this.point = point;
    this.state = state;
  }

  public MemberPoint(Member member, Order order, int point, int state, Date expiredAt) {
    this(member, order, point, state);
    this.earnedAt = new Date();
    this.expiredAt = expiredAt;
  }

  public void setCreatedAt(Date date) {
    super.createdAt = date;
  }
}
