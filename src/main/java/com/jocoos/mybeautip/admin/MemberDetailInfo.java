package com.jocoos.mybeautip.admin;

import org.springframework.beans.BeanUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.recommendation.MemberRecommendation;
import com.jocoos.mybeautip.schedules.Schedule;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class MemberDetailInfo extends MemberInfo {
  private MemberRecommendation recommendation;
  private int reportCount;
  private int link;
  private int point;
  private int revenue;
  private int publicVideoCount;
  private int totalVideoCount;
  private Schedule schedule;
  private boolean pushable;
  private int orderCount;

  public MemberDetailInfo(Member member) {
    super(member);
    BeanUtils.copyProperties(member, this);
  }

  public MemberDetailInfo(Member member, MemberRecommendation recommendation) {
    this(member);
    this.recommendation = recommendation;
  }
}
