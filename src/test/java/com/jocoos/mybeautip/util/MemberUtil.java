package com.jocoos.mybeautip.util;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.restapi.dto.SignupRequest;

public class MemberUtil {

    public static Member defaultMember(Long memberId, String socialId) {;
        final String grantType = "naver";
        final String empty = "";
        SignupRequest request = createRequest(socialId, grantType, empty);

        Member member = new Member(request);
        member.setId(memberId);
        return member;
    }

    private static SignupRequest createRequest(String socialId, String grantType, String empty) {
        SignupRequest request = new SignupRequest();
        request.setSocialId(socialId);
        request.setGrantType(grantType);
        request.setUsername(empty);
        request.setEmail(empty);
        request.setAvatarUrl(empty);
        return request;
    }
}
