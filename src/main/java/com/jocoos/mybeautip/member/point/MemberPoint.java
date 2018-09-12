package com.jocoos.mybeautip.member.point;

import javax.persistence.*;

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

  @Column
  private Date earnedAt;

  @Column
  private Date expiredAt;

  public MemberPoint(Member member, int point) {
    this(member, point, STATE_WILL_BE_EARNED);
  }

  public MemberPoint(Member member, int point, int state) {
    this.member = member;
    this.point = point;
    this.state = state;
  }

  public void setCreatedAt(Date date) {
    super.createdAt = date;
  }
}
