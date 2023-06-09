package com.jocoos.mybeautip.member;

import com.jocoos.mybeautip.client.aws.s3.AwsS3Handler;
import com.jocoos.mybeautip.domain.broadcast.service.dao.BroadcastPermissionDao;
import com.jocoos.mybeautip.domain.member.dto.MemberDetailRequest;
import com.jocoos.mybeautip.domain.member.dto.MemberDetailResponse;
import com.jocoos.mybeautip.domain.member.dto.SettingNotificationDto;
import com.jocoos.mybeautip.domain.member.persistence.domain.MemberDetail;
import com.jocoos.mybeautip.domain.member.persistence.repository.MemberDetailRepository;
import com.jocoos.mybeautip.domain.member.vo.ChangedTagInfo;
import com.jocoos.mybeautip.domain.point.service.ActivityPointService;
import com.jocoos.mybeautip.domain.slack.aspect.annotation.SendSlack;
import com.jocoos.mybeautip.domain.term.dto.TermTypeResponse;
import com.jocoos.mybeautip.domain.term.service.MemberTermService;
import com.jocoos.mybeautip.global.code.UrlDirectory;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import com.jocoos.mybeautip.global.util.StringConvertUtil;
import com.jocoos.mybeautip.log.MemberLeaveLog;
import com.jocoos.mybeautip.log.MemberLeaveLogRepository;
import com.jocoos.mybeautip.member.block.BlockService;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.member.report.Report;
import com.jocoos.mybeautip.member.report.ReportRepository;
import com.jocoos.mybeautip.member.vo.Birthday;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.restapi.LegacyMemberController;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import com.jocoos.mybeautip.support.AttachmentService;
import com.jocoos.mybeautip.video.Video;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.INPUT_EXTRA_INFO;
import static com.jocoos.mybeautip.domain.point.service.activity.ValidObject.validDomainIdAndReceiver;
import static com.jocoos.mybeautip.domain.slack.aspect.code.MessageType.MEMBER_REPORT;
import static com.jocoos.mybeautip.domain.slack.aspect.code.MessageType.MEMBER_WITHDRAWAL;
import static com.jocoos.mybeautip.global.constant.MybeautipConstant.DEFAULT_AVATAR_FILE_NAME;
import static com.jocoos.mybeautip.global.constant.MybeautipConstant.DEFAULT_AVATAR_URL;

@Slf4j
@RequiredArgsConstructor
@Service
public class LegacyMemberService {

    private static final String MEMBER_NOT_FOUND = "member.not_found";

    private final BlockService blockService;
    private final MessageService messageService;
    private final MemberTermService memberTermService;
    private final MemberRepository memberRepository;
    private final MemberDetailRepository memberDetailRepository;
    private final FollowingRepository followingRepository;
    private final ReportRepository reportRepository;
    private final FacebookMemberRepository facebookMemberRepository;
    private final KakaoMemberRepository kakaoMemberRepository;
    private final NaverMemberRepository naverMemberRepository;
    private final AppleMemberRepository appleMemberRepository;
    private final MemberLeaveLogRepository memberLeaveLogRepository;
    private final AttachmentService attachmentService;
    private final BroadcastPermissionDao broadcastPermissionDao;


    private final ActivityPointService activityPointService;
    private final AwsS3Handler awsS3Handler;
    private final String PATH_AVATAR = "avatar";
    private final String emailRegex = "[A-Za-z0-9_-]+[\\.\\+A-Za-z0-9_-]*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";

    @Value("${mybeautip.aws.cf.domain}")
    private String cloudFront;


    public Long currentMemberId() {
        return Optional.ofNullable(currentMember()).map(Member::getId).orElse(null);
    }

    public String getGuestUserName() {
        Object principal = currentPrincipal();
        if (principal instanceof MyBeautipUserDetails) {
            return ((MyBeautipUserDetails) principal).getUsername();
        } else {
            log.warn("Unknown principal type");
        }
        return null;
    }

    public Member currentMember() {
        Object principal = currentPrincipal();
        if (principal instanceof MyBeautipUserDetails) {
            return ((MyBeautipUserDetails) principal).getMember();
        } else {
            log.warn("Unknown principal type");
        }
        return null;
    }

    public Member getAdmin() {
        return memberRepository.findByUsernameAndDeletedAtIsNullAndVisibleIsTrue("마이뷰팁")
                .orElseThrow(() -> new MemberNotFoundException("Can't find admin"));
    }

    @Transactional(readOnly = true)
    public MemberMeInfo getMyInfo(String lang) {
        Long memberId = currentMemberId();
        List<TermTypeResponse> termTypeResponses =
                memberTermService.getOptionalTermAcceptStatus(memberId);

        MemberMeInfo memberMeInfo = memberRepository.findByIdAndDeletedAtIsNull(memberId)
                .map(m -> new MemberMeInfo(m, termTypeResponses))
                .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));

        memberMeInfo.setBroadcastPermission(broadcastPermissionDao.getBroadcastPermission(memberId));

        return memberMeInfo;

    }

    private Object currentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return authentication.getPrincipal();
    }

    public MyBeautipUserDetails currentUserDetails() {
        Object principal = currentPrincipal();
        if (principal instanceof MyBeautipUserDetails) {
            return (MyBeautipUserDetails) principal;
        } else {
            log.warn("Unknown principal type");
        }
        return null;
    }

    public boolean validPhoneNumber(String phoneNumber) {
        phoneNumber = phoneNumber.trim()
                .replace("-", "")
                .replace(" ", "");

        if (memberRepository.existsByPhoneNumber(phoneNumber)) {
            throw new BadRequestException(ErrorCode.DUPLICATE_PHONE, "duplicate_phone");
        }

        return true;
    }

    public Long getFollowingId(Long you) {
        if (currentMemberId() == null) {
            return null;
        }
        Optional<Following> optional
                = followingRepository.findByMemberMeIdAndMemberYouId(currentMemberId(), you);
        return optional.map(Following::getId).orElse(null);
    }

    public Long getFollowingId(Member member) {
        if (currentMemberId() == null) {
            return null;
        }
        Optional<Following> optional
                = followingRepository.findByMemberMeIdAndMemberYouId(currentMemberId(), member.getId());
        return optional.map(Following::getId).orElse(null);
    }

    public MemberExtraInfo getMemberExtraInfo(Member member) {
        if (currentMemberId() == null) {
            return null;
        }
        Optional<MemberExtraInfo> optional = memberRepository.findMemberExtraInfo(currentMemberId(), member.getId());
        return optional.orElse(null);
    }

    public MemberInfo getMemberInfo(Member member) {
        if (currentMember() != null && member.getId().equals(currentMember().getId())) {
            return new MemberMeInfo(member);
        } else {
            return new MemberInfo(member, getMemberExtraInfo(member));
        }
    }

    public void checkEmailValidation(String email, String lang) {
        if (email.length() > 50 || !email.matches(emailRegex)) {
            throw new BadRequestException("Email does not match the format");
        }
    }

    public boolean hasCommentPostPermission(Member member) {
        return ((member.getPermission() & Member.COMMENT_POST) == Member.COMMENT_POST);
    }

    @Transactional
    public void updateLastLoggedAt() {
        Member member = currentMember();
        if (member != null) {
            updateLastLoggedAt(member.getId());
        }
    }

    @Transactional
    public void updateLastLoggedAt(long memberId) {
        memberRepository.updateLastLoggedAt(memberId);
    }

    @SendSlack(messageType = MEMBER_REPORT)
    @Transactional
    public Report reportMember(Member me, long targetId, int reasonCode, String reason, Video video, String lang) {
        Member you = memberRepository.findByIdAndDeletedAtIsNull(targetId)
                .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));

        Report report = reportRepository.save(new Report(me, you, reasonCode, reason, video));
        memberRepository.updateReportCount(you.getId(), 1);

        blockService.blockMember(me.getId(), targetId);

        return report;
    }

    @Transactional
    public void readMemberRevenue(Member member) {
        member.setRevenueModifiedAt(null);
        memberRepository.save(member);
    }

    @Transactional
    public Member updateMember(LegacyMemberController.UpdateMemberRequest request, Member member) {
        boolean isFirstUpdate = !member.isVisible();
        String originalAvatar = member.getAvatarUrl();

        if (request.getUsername() != null) {
            member.setUsername(request.getUsername());
        }

        String avatarUrl = uploadAvatarAndGet(request.getAvatarUrl());
        member.setAvatarFilenameFromUrl(avatarUrl);

        member.setVisible(true);
        Member finalMember = memberRepository.save(member);

        if (isFirstUpdate) { // when first called
            // Follow Admin member as default
            memberRepository.findByUsernameAndLinkAndDeletedAtIsNull("마이뷰팁", 0)
                    .ifPresent(adminMember -> {
                        followMember(finalMember, adminMember);
                        finalMember.setFollowingCount(1); // for response view
                    });
        }

        deleteOriginalAvatarIfChange(member, originalAvatar);

        return finalMember;
    }

    private void deleteOriginalAvatarIfChange(Member member, String originalAvatar) {
        if (!member.isAvatarUrlSame(originalAvatar)) {
            deleteAvatar(originalAvatar);
        }
    }

    private String uploadAvatarAndGet(String avatarUrl) {
        if (isUrlFromOuter(avatarUrl)) {
            return awsS3Handler.upload(avatarUrl, UrlDirectory.AVATAR.getDirectory(), DEFAULT_AVATAR_FILE_NAME);
        } else {
            return awsS3Handler.copy(avatarUrl, UrlDirectory.AVATAR.getDirectory(), DEFAULT_AVATAR_FILE_NAME);
        }
    }

    private boolean isUrlFromOuter(String avatarUrl) {
        return !StringUtils.isBlank(avatarUrl) && !avatarUrl.startsWith(cloudFront);
    }

    @SendSlack(messageType = MEMBER_WITHDRAWAL)
    @Transactional
    public MemberLeaveLog deleteMember(LegacyMemberController.DeleteMemberRequest request, Member member) {
        int link = member.getLink();
        switch (link) {
            case 1:
                facebookMemberRepository.findByMemberId(member.getId()).ifPresent(facebookMemberRepository::delete);
                break;

            case 2:
                naverMemberRepository.findByMemberId(member.getId()).ifPresent(naverMemberRepository::delete);
                break;

            case 4:
                kakaoMemberRepository.findByMemberId(member.getId()).ifPresent(kakaoMemberRepository::delete);
                break;

            case 8:
                appleMemberRepository.findByMemberId(member.getId()).ifPresent(appleMemberRepository::delete);
                break;
            default:
                throw new BadRequestException("Not supported member link: " + link);
        }

        member.setIntro("");
        member.setAvatarFilename(DEFAULT_AVATAR_FILE_NAME);
        member.setVisible(false);
        member.setFollowingCount(0);
        member.setFollowerCount(0);
        member.setPublicVideoCount(0);
        member.setTotalVideoCount(0);
        member.setDeletedAt(new Date());
        memberRepository.saveAndFlush(member);

        log.debug(String.format("Member deleted: %d, %s, %s", member.getId(), member.getUsername(), member.getDeletedAt()));
        return memberLeaveLogRepository.save(new MemberLeaveLog(member, request.getReason()));
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public Following followMember(Member me, Member you) {
        memberRepository.updateFollowingCount(me.getId(), 1);
        memberRepository.updateFollowerCount(you.getId(), 1);
        return followingRepository.save(new Following(me, you));
    }

    @Transactional
    public void unFollowMember(Following following) {
        followingRepository.delete(following);
        if (following.getMemberMe().getFollowingCount() > 0) {
            memberRepository.updateFollowingCount(following.getMemberMe().getId(), -1);
        }

        if (following.getMemberYou().getFollowerCount() > 0) {
            memberRepository.updateFollowerCount(following.getMemberYou().getId(), -1);
        }
    }

    @Transactional(readOnly = true)
    public MemberDetailResponse getDetailInfo(long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("No such Member. id - " + memberId));

        MemberDetail memberDetail = memberDetailRepository.findByMemberId(memberId)
                .orElse(null);

        String tag = "";
        if (memberDetail != null && memberDetail.getInviterId() != null) {
            Member inverterMember = memberRepository.findById(memberDetail.getInviterId()).orElse(null);
            tag = inverterMember != null ? inverterMember.getTag() : "";
        }

        return MemberDetailResponse.builder()
                .ageGroup(member.getBirthday().getAgeGroupByTen())
                .skinType(memberDetail != null ? memberDetail.getSkinType() : null)
                .skinWorry(memberDetail != null ? memberDetail.getSkinWorry() : new HashSet<>())
                .inviterTag(tag)
                .build();
    }

    @Transactional
    public MemberDetailResponse updateDetailInfo(MemberDetailRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("No such member. id - " + request.getMemberId()));

        member.setBirthday(new Birthday(request.getAgeGroup()));
        memberRepository.save(member);

        MemberDetail memberDetail = memberDetailRepository.findByMemberId(request.getMemberId())
                .orElse(new MemberDetail(request.getMemberId()));

        // FIXME 친구초대 이벤트를 위해서 회원 상세 정보 수정 메소드에서 이벤트 Aspect 에서 쓸 데이터(ChangedTagInfo)를 가공한다. 꼴뵈기 싫으...
        Member targetMember = null;
        boolean isTagChanged = false;
        if (StringUtils.isNotBlank(request.getInviterTag())) {
            targetMember = memberRepository.findByTag(request.getInviterTag())
                    .orElseThrow(() -> new MemberNotFoundException("No such Member. tag - " + request.getInviterTag()));

            if (member.getId().equals(targetMember.getId())) {
                throw new BadRequestException(ErrorCode.MY_TAG, "Target member is me.");
            }

            if (memberDetail.getInviterId() == null) {
                isTagChanged = true;
            }
        }

        ChangedTagInfo changedTagInfo = ChangedTagInfo.builder()
                .isChanged(isTagChanged)
                .member(member)
                .targetMember(targetMember)
                .build();

        memberDetail.setSkinType(request.getSkinType());
        memberDetail.setSkinWorry(request.getSkinWorry());
        if (targetMember != null) {
            memberDetail.registerInviterId(targetMember.getId());
        }

        memberDetail = memberDetailRepository.save(memberDetail);
        activityPointService.gainActivityPoint(INPUT_EXTRA_INFO, validDomainIdAndReceiver(memberDetail.getId(), member));

        return MemberDetailResponse.builder()
                .ageGroup(member.getBirthday().getAgeGroupByTen())
                .skinType(memberDetail.getSkinType())
                .skinWorry(memberDetail.getSkinWorry())
                .inviterTag(request.getInviterTag())
                .changedTagInfo(changedTagInfo)
                .build();
    }

    public void deleteAvatar(String avatar) {
        if (StringUtils.isNotBlank(avatar) && !DEFAULT_AVATAR_URL.equals(avatar)) {
            attachmentService.deleteAttachments(StringConvertUtil.getPath(avatar));
        }
    }

    @Transactional
    public MemberInfo updateSettingNotification(long memberId, SettingNotificationDto request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("No such Member. id - " + memberId));

        return updateSettingNotification(member, request);
    }

    @Transactional
    public MemberInfo updateSettingNotification(Member member, SettingNotificationDto request) {
        member.setPushable(request.isPushable());

        memberRepository.save(member);
        return getMemberInfo(member);
    }
}
