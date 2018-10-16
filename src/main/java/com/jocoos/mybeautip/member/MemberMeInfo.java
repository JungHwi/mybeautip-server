package com.jocoos.mybeautip.member;

import org.springframework.beans.BeanUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class MemberMeInfo extends MemberInfo {
  private int point;
  private int revenue;

  private int pointRatio;
  private int revenueRatio;

  public MemberMeInfo(Member member) {
    BeanUtils.copyProperties(member, this);
    this.setVideoCount(member.getTotalVideoCount());
  }

  public MemberMeInfo(Member member, int pointRatio, int revenueRatio) {
    this(member);
    this.pointRatio = pointRatio;
    this.revenueRatio = revenueRatio;
  }
}
