package com.jocoos.mybeautip.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;

import com.jocoos.mybeautip.member.Member;

@Getter
public class MyBeautipUserDetails extends User {

  private Member member;

  public MyBeautipUserDetails(Member member) {
    super(member.getId().toString(), "", AuthorityUtils.createAuthorityList("ROLE_USER"));
    this.member = member;
  }
}
