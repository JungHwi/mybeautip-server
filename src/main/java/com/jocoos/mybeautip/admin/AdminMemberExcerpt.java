package com.jocoos.mybeautip.admin;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.core.config.Projection;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.store.Store;

@Projection(name = "admin_member", types = AdminMember.class)
public interface AdminMemberExcerpt {

  String getEmail();

  @Value("#{target.member}")
  Member getMember();

  @Value("#{target.store}")
  Store getStore();
}
