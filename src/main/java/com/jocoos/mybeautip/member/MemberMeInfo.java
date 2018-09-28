package com.jocoos.mybeautip.member;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;

import com.google.common.base.Strings;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.restapi.VideoController;

@NoArgsConstructor
@Data
public class MemberMeInfo extends MemberInfo {
  private int revenue;

  public MemberMeInfo(Member member) {
    BeanUtils.copyProperties(member, this);
  }
}
