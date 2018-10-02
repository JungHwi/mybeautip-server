package com.jocoos.mybeautip.member;

import org.springframework.beans.BeanUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class MemberMeInfo extends MemberInfo {
  private int revenue;

  public MemberMeInfo(Member member) {
    BeanUtils.copyProperties(member, this);
  }
}
