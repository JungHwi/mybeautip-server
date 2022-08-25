package com.jocoos.mybeautip.member;

import com.jocoos.mybeautip.domain.member.converter.MemberConverter;
import com.jocoos.mybeautip.domain.member.dto.MemberDetailRequest;
import com.jocoos.mybeautip.domain.member.dto.MemberDetailResponse;
import com.jocoos.mybeautip.domain.member.dto.SettingNotificationDto;
import com.jocoos.mybeautip.domain.member.persistence.domain.MemberDetail;
import com.jocoos.mybeautip.domain.member.persistence.repository.MemberDetailRepository;
import com.jocoos.mybeautip.domain.member.service.SocialMemberService;
import com.jocoos.mybeautip.domain.member.service.social.SocialMemberFactory;
import com.jocoos.mybeautip.domain.member.vo.ChangedTagInfo;
import com.jocoos.mybeautip.domain.point.service.ActivityPointService;
import com.jocoos.mybeautip.domain.point.service.activity.ValidObject;
import com.jocoos.mybeautip.global.constant.RegexConstants;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.MemberNotFoundException;
import com.jocoos.mybeautip.global.exception.MybeautipException;
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
import com.jocoos.mybeautip.restapi.dto.SignupRequest;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import com.jocoos.mybeautip.support.AttachmentService;
import com.jocoos.mybeautip.support.RandomUtils;
import com.jocoos.mybeautip.support.slack.SlackService;
import com.jocoos.mybeautip.tag.TagService;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.word.BannedWord;
import com.jocoos.mybeautip.word.BannedWordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.INPUT_ADDITIONAL_INFO;
import static com.jocoos.mybeautip.domain.point.code.ActivityPointType.INPUT_EXTRA_INFO;
import static com.jocoos.mybeautip.domain.point.service.activity.ValidObject.validDomainIdAndReceiver;
import static com.jocoos.mybeautip.global.constant.MybeautipConstant.DEFAULT_AVATAR_FILE_NAME;
import static com.jocoos.mybeautip.global.constant.MybeautipConstant.DEFAULT_AVATAR_URL;

@Slf4j
@Service
public class LegacyMemberService {

    private static final String USERNAME_INVALID_LENGTH = "username.invalid_length";
    private static final String USERNAME_INVALID_FORMAT = "username.invalid_format";
    private static final String USERNAME_INVALID_CHAR = "username.invalid_char";
    private static final String USERNAME_ALREADY_USED = "username.already_used";
    private static final String EMAIL_INVALID_FORMAT = "email.invalid_format";
    private static final String MEMBER_NOT_FOUND = "member.not_found";
    private final BlockService blockService;
    private final BannedWordService bannedWordService;
    private final MessageService messageService;
    private final TagService tagService;
    private final MemberRepository memberRepository;
    private final MemberDetailRepository memberDetailRepository;
    private final FollowingRepository followingRepository;
    private final ReportRepository reportRepository;
    private final FacebookMemberRepository facebookMemberRepository;
    private final KakaoMemberRepository kakaoMemberRepository;
    private final NaverMemberRepository naverMemberRepository;
    private final AppleMemberRepository appleMemberRepository;
    private final MemberLeaveLogRepository memberLeaveLogRepository;
    private final MemberConverter memberConverter;
    private final AttachmentService attachmentService;
    private final SlackService slackService;
    private final SocialMemberFactory socialMemberFactory;

    private final ActivityPointService activityPointService;

    private final String PATH_AVATAR = "avatar";
    private final String emailRegex = "[A-Za-z0-9_-]+[\\.\\+A-Za-z0-9_-]*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";

    public LegacyMemberService(BannedWordService bannedWordService,
                               MessageService messageService,
                               TagService tagService,
                               MemberRepository memberRepository,
                               MemberDetailRepository memberDetailRepository,
                               FollowingRepository followingRepository,
                               ReportRepository reportRepository,
                               BlockService blockService,
                               FacebookMemberRepository facebookMemberRepository,
                               KakaoMemberRepository kakaoMemberRepository,
                               NaverMemberRepository naverMemberRepository,
                               AppleMemberRepository appleMemberRepository,
                               MemberLeaveLogRepository memberLeaveLogRepository,
                               MemberConverter memberConverter,
                               AttachmentService attachmentService,
                               SlackService slackService,
                               SocialMemberFactory socialMemberFactory,
                               ActivityPointService activityPointService) {
        this.bannedWordService = bannedWordService;
        this.messageService = messageService;
        this.tagService = tagService;
        this.memberRepository = memberRepository;
        this.memberDetailRepository = memberDetailRepository;
        this.followingRepository = followingRepository;
        this.reportRepository = reportRepository;
        this.blockService = blockService;
        this.facebookMemberRepository = facebookMemberRepository;
        this.kakaoMemberRepository = kakaoMemberRepository;
        this.naverMemberRepository = naverMemberRepository;
        this.appleMemberRepository = appleMemberRepository;
        this.memberLeaveLogRepository = memberLeaveLogRepository;
        this.memberConverter = memberConverter;
        this.attachmentService = attachmentService;
        this.slackService = slackService;
        this.socialMemberFactory = socialMemberFactory;
        this.activityPointService = activityPointService;
    }

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
            throw new BadRequestException("duplicate_phone", "duplicate_phone");
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

    public void checkUsernameValidation(String username, String lang) {
        checkUsernameValidation(0L, username, lang);
    }

    public void checkUsernameValidation(Long userId, String username, String lang) {
        if (!(username.matches(RegexConstants.regexForUsername))) {
            throw new BadRequestException("invalid_char", messageService.getMessage(USERNAME_INVALID_CHAR, lang));
        }

        List<Member> otherMembers = memberRepository.findByUsername(username).stream()
                .filter(i -> i.getId() != userId)
                .collect(Collectors.toList());

        if (!otherMembers.isEmpty()) {
            throw new BadRequestException("already_used", messageService.getMessage(USERNAME_ALREADY_USED, lang));
        }

        bannedWordService.findWordAndThrowException(BannedWord.CATEGORY_USERNAME, username, lang);
    }

    public void checkEmailValidation(String email, String lang) {
        if (email.length() > 50 || !email.matches(emailRegex)) {
            throw new BadRequestException("invalid_email", messageService.getMessage(EMAIL_INVALID_FORMAT, lang));
        }
    }

    public boolean hasCommentPostPermission(Member member) {
        return ((member.getPermission() & Member.COMMENT_POST) == Member.COMMENT_POST);
    }

    // Check ASPECT
    @Transactional
    public Member register(SignupRequest signupRequest) {
        Member member = new Member(signupRequest);
        member = adjustUniqueInfo(member);
        Member registeredMember = memberRepository.save(member);

        SocialMemberService socialMemberService = socialMemberFactory.getSocialMemberService(member.getLink());
        socialMemberService.save(signupRequest, member.getId());

        return registeredMember;
    }

    public Member adjustUniqueInfo(Member member) {
        member = adjustTag(member);
        return adjustUserName(member);
    }

    private Member adjustTag(Member member) {
        String tag = member.getTag();
        for (int retry = 0; retry < 5; retry++) {
            if (StringUtils.isNotBlank(tag) && !memberRepository.existsByTag(tag)) {
                member.setTag(tag);
                return member;
            }
            tag = RandomUtils.generateTag();
        }

        log.warn("Member is Duplicate Tag. Id : " + member.getId());
        slackService.duplicateTag(member.getId());
        return member;
    }

    public Member adjustUserName(Member member) {
        String username = member.getUsername();
        try {
            checkUsernameValidation(member.getId(), username, Locale.KOREAN.getLanguage());
        } catch (BadRequestException ex) {
            for (int retry = 0; retry < 5; retry++) {
                username = RandomUtils.generateUsername();
                if (!memberRepository.existsByUsername(username)) {
                    member.setUsername(username);
                    return member;
                }
            }
            slackService.duplicateUsername(member.getUsername());
        }
        return member;
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

        member.setAvatarFilenameFromUrl(request.getAvatarUrl());

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

        deleteAvatar(originalAvatar);
        activityPointService.gainActivityPoint(INPUT_ADDITIONAL_INFO, ValidObject.validReceiver(member));
        return finalMember;
    }

    @Transactional
    public void deleteMember(LegacyMemberController.DeleteMemberRequest request, Member member) {
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
                throw new BadRequestException("invalid_member_link", "invalid member link: " + link);
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
        memberLeaveLogRepository.save(new MemberLeaveLog(member, request.getReason()));
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
                throw new BadRequestException("my_tag", "Target member is me.");
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
            memberDetail.setInviterId(targetMember.getId());
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

    public String uploadAvatar(MultipartFile avatar) {
        String path = "";
        try {
            if (avatar != null) {
                path = attachmentService.upload(avatar, PATH_AVATAR);
            } else {
                path = DEFAULT_AVATAR_URL;
            }

        } catch (IOException ex) {
            throw new MybeautipException("Member avatar upload Error.");
        }

        return path;
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
