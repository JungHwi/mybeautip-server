package com.jocoos.mybeautip.domain.member.service;

import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
import com.jocoos.mybeautip.domain.member.dto.MemberEntireInfo;
import com.jocoos.mybeautip.domain.member.service.social.SocialMemberFactory;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.log.MemberLeaveLog;
import com.jocoos.mybeautip.log.MemberLeaveLogRepository;
import com.jocoos.mybeautip.member.*;
import com.jocoos.mybeautip.restapi.dto.SignupRequest;
import com.jocoos.mybeautip.security.AccessTokenResponse;
import com.jocoos.mybeautip.security.AppleLoginService;
import com.jocoos.mybeautip.security.JwtTokenProvider;
import com.jocoos.mybeautip.support.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static com.jocoos.mybeautip.global.constant.MybeautipConstant.DELETED_AVATAR_URL;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberSignupService {

    private final SocialMemberFactory socialMemberFactory;
    private final MemberService memberService;
    private final LegacyMemberService legacyMemberService;
    private final AppleLoginService appleLoginService;

    private final MemberRepository memberRepository;
    private final MemberLeaveLogRepository memberLeaveLogRepository;

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberConverter memberConverter;
    private final KakaoMemberRepository kakaoMemberRepository;

    @Value("${mybeautip.service.member.rejoin-available-second}")
    private long REJOIN_AVAILABLE_SECOND;

    @Transactional
    public MemberEntireInfo signup(SignupRequest request) {
        validSignup(request);

        Member member = legacyMemberService.register(request);

        MemberEntireInfo memberEntireInfo = memberConverter.convert(member);

        AccessTokenResponse accessTokenResponse = jwtTokenProvider.auth(member);
        memberEntireInfo.setToken(accessTokenResponse);

        return memberEntireInfo;
    }

    @Transactional
    public void withdrawal(String reason) {
        Member member = legacyMemberService.currentMember();
        member.setStatus(MemberStatus.WITHDRAWAL);
        member.setVisible(false);
        member.setAvatarUrl(DELETED_AVATAR_URL);
        member.setFollowingCount(0);
        member.setFollowerCount(0);
        member.setPublicVideoCount(0);
        member.setTotalVideoCount(0);
        member.setDeletedAt(new Date());

        memberRepository.save(member);

        if (member.getLink() == Member.LINK_APPLE) {
            appleLoginService.revoke(member.getId());
        }
        memberLeaveLogRepository.save(new MemberLeaveLog(member, reason));
    }

    private void validSignup(SignupRequest signupRequest) {
        SocialMemberService socialMemberService = socialMemberFactory.getSocialMemberService(signupRequest.getGrantType());

        SocialMember socialMember = socialMemberService.findById(signupRequest.getSocialId());

        if (socialMember == null) {
            return;
        }

        Long memberId = socialMember.getMemberId();
        Member member = memberService.findById(memberId);

        switch (member.getStatus()) {
            case ACTIVE:
                throw new BadRequestException("already_member", "already_member");
            case DORMANT:
                throw new BadRequestException("dormant_member", "dormant_member");
            case WITHDRAWAL:
                LocalDateTime availableRejoin = DateUtils.toLocalDateTime(member.getDeletedAt(), ZoneId.systemDefault()).plusSeconds(REJOIN_AVAILABLE_SECOND);
                if (availableRejoin.isAfter(LocalDateTime.now())) {
                    throw new BadRequestException("not_yet_rejoin", "not_yet_rejoin");
                }
        }
    }
}