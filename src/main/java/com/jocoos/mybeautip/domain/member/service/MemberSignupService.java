package com.jocoos.mybeautip.domain.member.service;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.member.code.MemberStatus;
import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
import com.jocoos.mybeautip.domain.member.dto.MemberEntireInfo;
import com.jocoos.mybeautip.domain.member.service.dao.MemberActivityCountDao;
import com.jocoos.mybeautip.domain.member.service.social.SocialMemberFactory;
import com.jocoos.mybeautip.domain.term.code.TermType;
import com.jocoos.mybeautip.domain.term.service.MemberTermService;
import com.jocoos.mybeautip.global.code.UrlDirectory;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.log.MemberLeaveLog;
import com.jocoos.mybeautip.log.MemberLeaveLogRepository;
import com.jocoos.mybeautip.member.LegacyMemberService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.SocialMember;
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
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;

import static com.jocoos.mybeautip.domain.term.code.TermTypeGroup.isAllRequiredContains;
import static com.jocoos.mybeautip.global.constant.MybeautipConstant.DEFAULT_AVATAR_FILE_NAME;

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
    private final MemberTermService memberTermService;
    private final MemberActivityCountDao activityCountDao;

    private final AwsS3Handler awsS3Handler;

    @Value("${mybeautip.service.member.rejoin-available-second}")
    private long REJOIN_AVAILABLE_SECOND;

    @Transactional
    public MemberEntireInfo signup(SignupRequest request) {
        validSignup(request);

        savaAndSetAvatarUrlIfExists(request);

        Member member = legacyMemberService.register(request);
        activityCountDao.initActivityCount(member);

        MemberEntireInfo memberEntireInfo = memberConverter.convert(member);

        AccessTokenResponse accessTokenResponse = jwtTokenProvider.auth(member);
        memberEntireInfo.setToken(accessTokenResponse);

        memberTermService.chooseTermsByTermType(request.getTermTypes(), member);
        return memberEntireInfo;
    }

    @Transactional
    public void withdrawal(String reason) {
        Member member = legacyMemberService.currentMember();
        member.setStatus(MemberStatus.WITHDRAWAL);
        member.setVisible(false);
        member.setAvatarFilename(DEFAULT_AVATAR_FILE_NAME);
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
        validAllRequiredTermsExist(signupRequest.getTermTypes());

        SocialMemberService socialMemberService = socialMemberFactory.getSocialMemberService(signupRequest.getGrantType());

        SocialMember socialMember = socialMemberService.findById(signupRequest.getSocialId());

        if (socialMember == null) {
            return;
        }

        Long memberId = socialMember.getMemberId();
        Member member = memberService.findById(memberId);

        switch (member.getStatus()) {
            case ACTIVE:
                throw new BadRequestException(ErrorCode.ALREADY_MEMBER, "already_member");
            case DORMANT:
                throw new BadRequestException(ErrorCode.DORMANT_MEMBER, "dormant_member");
            case WITHDRAWAL:
                LocalDateTime availableRejoin = DateUtils.toLocalDateTime(member.getDeletedAt(), ZoneId.systemDefault()).plusSeconds(REJOIN_AVAILABLE_SECOND);
                if (availableRejoin.isAfter(LocalDateTime.now())) {
                    throw new BadRequestException(ErrorCode.NOT_YET_REJOIN, "not_yet_rejoin");
                }
        }
    }

    private void validAllRequiredTermsExist(Set<TermType> termTypes) {
        if (!isAllRequiredContains(termTypes)) {
            throw new BadRequestException("required terms have to all check");
        }
    }

    private void savaAndSetAvatarUrlIfExists(SignupRequest request) {
        if (StringUtils.hasText(request.getAvatarUrl())) {
            String uploadAvatarUrl =
                    awsS3Handler.upload(request.getAvatarUrl(), UrlDirectory.AVATAR.getDirectory(), DEFAULT_AVATAR_FILE_NAME);
            request.changeAvatarUrl(uploadAvatarUrl);
        }
    }
}
