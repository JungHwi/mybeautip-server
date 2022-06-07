package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.member.Member;
import lombok.Getter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;

@Getter
public class MyBeautipUserDetails extends User {

    private Member member;

    public MyBeautipUserDetails(Member member) {
        super(member.getId().toString(), "", AuthorityUtils.createAuthorityList("ROLE_USER"));
        this.member = member;
    }

    public MyBeautipUserDetails(Member member, String... roles) {
        super(member.getId().toString(), "", AuthorityUtils.createAuthorityList(roles));
        this.member = member;
    }

    public MyBeautipUserDetails(String username, String authority) {
        super(username, "", AuthorityUtils.createAuthorityList(authority));
    }
}
