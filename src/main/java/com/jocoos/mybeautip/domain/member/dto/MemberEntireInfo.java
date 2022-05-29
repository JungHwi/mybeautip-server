package com.jocoos.mybeautip.domain.member.dto;

import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.security.AccessTokenResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MemberEntireInfo {

    private MemberInfo member;

    private AccessTokenResponse token;

}
