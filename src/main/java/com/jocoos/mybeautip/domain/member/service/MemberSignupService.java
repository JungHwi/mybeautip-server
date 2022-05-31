package com.jocoos.mybeautip.domain.member.service;

import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
import com.jocoos.mybeautip.domain.member.converter.SocialMemberConverter;
import com.jocoos.mybeautip.domain.member.dto.MemberEntireInfo;
import com.jocoos.mybeautip.domain.member.service.social.SocialMemberFactory;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.restapi.dto.SignupRequest;
import com.jocoos.mybeautip.security.AccessTokenResponse;
import com.jocoos.mybeautip.security.JwtTokenProvider;
import com.jocoos.mybeautip.security.SocialMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberSignupService {

    private final MemberService memberService;
    private final SocialMemberFactory socialMemberFactory;

    private final JwtTokenProvider jwtTokenProvider;

    private final MemberConverter memberConverter;
    private final SocialMemberConverter socialMemberConverter;

    @Transactional
    public MemberEntireInfo signup(SignupRequest request) {
        SocialMember socialMember = socialMemberConverter.convert(request);
        validSignup(socialMember);

        Member member = memberService.register(socialMember);

        MemberEntireInfo memberEntireInfo = memberConverter.convert(member);

        AccessTokenResponse accessTokenResponse = jwtTokenProvider.auth(member);
        memberEntireInfo.setToken(accessTokenResponse);

        return memberEntireInfo;
    }

    public void validSignup(SocialMember socialMember) {
        SocialMemberService socialMemberService = socialMemberFactory.getSocialMemberService(socialMember.getProvider());
        if (socialMemberService.exists(socialMember.getId())) {
            throw new BadRequestException("already_member");
        }
    }
}