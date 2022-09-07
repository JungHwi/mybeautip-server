package com.jocoos.mybeautip.global.util;

import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.restapi.dto.SignupRequest;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
public class TestMemberUtil {

    @Autowired
    private MemberRepository memberRepository;

    @Getter
    private Member member;

    public void defaultTestSetting() {
        final long memberId = 0L;
        final String socialId = "testSocialId";
        member = memberRepository.save(defaultMember(memberId, socialId));
    }

    public void defaultTestEnd() {
        memberRepository.delete(member);
    }

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
