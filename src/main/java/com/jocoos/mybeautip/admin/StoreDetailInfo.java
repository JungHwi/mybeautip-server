package com.jocoos.mybeautip.admin;

import org.springframework.beans.BeanUtils;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.recommendation.MemberRecommendation;
import com.jocoos.mybeautip.restapi.StoreController;
import com.jocoos.mybeautip.store.Store;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Data
public class StoreDetailInfo {
  private String email;
  private Long goodsCount;
  private MemberInfo member;
  private StoreController.StoreInfo store;

  public StoreDetailInfo(AdminMember adminMember) {
    this.email = adminMember.getEmail();
    member = new MemberInfo();
    store = new StoreController.StoreInfo();

    BeanUtils.copyProperties(adminMember.getMember(), member);
    BeanUtils.copyProperties(adminMember.getStore(), store);
  }
}
