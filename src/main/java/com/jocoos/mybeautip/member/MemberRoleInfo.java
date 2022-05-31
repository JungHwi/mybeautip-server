package com.jocoos.mybeautip.member;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = false)
@Data
public class MemberRoleInfo extends MemberInfo {
    private int role;
    private int storeId;

    public MemberRoleInfo(Member member, int role) {
        super(member);
        this.role = role;
    }
}
