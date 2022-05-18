package com.jocoos.mybeautip.member;

import com.jocoos.mybeautip.domain.member.dto.SettingNotificationDto;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.MybeautipRuntimeException;
import com.jocoos.mybeautip.global.util.StringConvertUtil;
import com.jocoos.mybeautip.log.MemberLeaveLog;
import com.jocoos.mybeautip.log.MemberLeaveLogRepository;
import com.jocoos.mybeautip.member.block.BlockService;
import com.jocoos.mybeautip.member.coupon.CouponService;
import com.jocoos.mybeautip.member.detail.MemberDetail;
import com.jocoos.mybeautip.member.detail.MemberDetailRepository;
import com.jocoos.mybeautip.member.detail.MemberDetailRequest;
import com.jocoos.mybeautip.member.detail.MemberDetailResponse;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.member.report.Report;
import com.jocoos.mybeautip.member.report.ReportRepository;
import com.jocoos.mybeautip.member.vo.Birthday;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.restapi.MemberController;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import com.jocoos.mybeautip.support.AttachmentService;
import com.jocoos.mybeautip.support.RandomUtils;
import com.jocoos.mybeautip.support.slack.SlackService;
import com.jocoos.mybeautip.tag.TagService;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.word.BannedWordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class MemberService {

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
  private final CouponService couponService;

  private final AttachmentService attachmentService;
  private final SlackService slackService;

  private final String PATH_AVATAR = "avatar";
  private final String emailRegex = "[A-Za-z0-9_-]+[\\.\\+A-Za-z0-9_-]*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";
  private final String defaultAvatarUrl = "https://mybeautip.s3.ap-northeast-2.amazonaws.com/avatar/img_profile_default.png";
  
  private static final String USERNAME_INVALID_LENGTH = "username.invalid_length";
  private static final String USERNAME_INVALID_FORMAT = "username.invalid_format";
  private static final String USERNAME_INVALID_CHAR = "username.invalid_char";
  private static final String USERNAME_ALREADY_USED = "username.already_used";
  private static final String EMAIL_INVALID_FORMAT = "email.invalid_format";
  private static final String MEMBER_NOT_FOUND = "member.not_found";

  public MemberService(BannedWordService bannedWordService,
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
                       AppleMemberRepository appleMemberRepository, MemberLeaveLogRepository memberLeaveLogRepository,
                       CouponService couponService,
                       AttachmentService attachmentService,
                       SlackService slackService) {
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
    this.couponService = couponService;
    this.attachmentService = attachmentService;
    this.slackService = slackService;
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

  public void checkUsernameValidation(@RequestParam String username, String lang) {
    Member me = currentMember();
    
    if (username.length() < 2 || username.length() > 25) {
      throw new BadRequestException("invalid_length", messageService.getMessage(USERNAME_INVALID_LENGTH, lang));
    }

    if (StringUtils.isNumeric(username)) {
      throw new BadRequestException("invalid_number", messageService.getMessage(USERNAME_INVALID_FORMAT, lang));
    }

    String regex = "^[\\w가-힣!_~]+$";
    if (!(username.matches(regex))) {
      throw new BadRequestException("invalid_char", messageService.getMessage(USERNAME_INVALID_CHAR, lang));
    }

    if (me.isVisible()) { // Already registered member
      if (!me.getUsername().equals(username)) {
        if (memberRepository.countByVisibleIsTrueAndUsernameAndDeletedAtIsNull(username) > 0) {
          throw new BadRequestException("already_used", messageService.getMessage(USERNAME_ALREADY_USED, lang));
        }
        if (memberRepository.countByUsernameAndLinkAndDeletedAtIsNull(username, Member.LINK_STORE) > 0) {
          throw new BadRequestException("already_used", messageService.getMessage(USERNAME_ALREADY_USED, lang));
        }
      }
    } else {
      if (memberRepository.countByVisibleIsTrueAndUsernameAndDeletedAtIsNull(username) > 0) {
        throw new BadRequestException("already_used", messageService.getMessage(USERNAME_ALREADY_USED, lang));
      }
      if (memberRepository.countByUsernameAndLinkAndDeletedAtIsNull(username, Member.LINK_STORE) > 0) {
        throw new BadRequestException("already_used", messageService.getMessage(USERNAME_ALREADY_USED, lang));
      }
    }

    bannedWordService.findWordAndThrowException(username, lang);
  }
  
  public void checkEmailValidation(String email, String lang) {
    if (email.length() > 50 || !email.matches(emailRegex)) {
      throw new BadRequestException("invalid_email", messageService.getMessage(EMAIL_INVALID_FORMAT, lang));
    }
  }
  
  public boolean hasCommentPostPermission(Member member) {
    return ((member.getPermission() & Member.COMMENT_POST) == Member.COMMENT_POST);
  }

    @Transactional
    public void tagMigration() {
        List<Member> noTagMembers = memberRepository.selectTagIsEmpty();

        for (Member member : noTagMembers) {
            adjustTag(member);
            memberRepository.save(member);
        }
    }

    @Transactional
    public Member register(Member member) {
        adjustTag(member);

        return memberRepository.save(member);
    }

    @Transactional
    public Member register(Map<String, String> params) {
        Member member = new Member(params);
        adjustTag(member);

        return memberRepository.save(member);
    }

    // TODO Migration 하고 나면 private 으로 변경.
    public Member adjustTag(Member member) {
        String tag = member.getTag();
        for (int retry = 0; retry < 5; retry++) {
           if (StringUtils.isNotBlank(tag) && memberRepository.countByTag(tag) == 0) {
              member.setTag(tag);
              return member;
           }
           tag = RandomUtils.generateTag();
        }

        log.warn("Duplicate Tag : " + member.getUsername());
        slackService.duplicateTag(member.getUsername());
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
  public Member updateMember(MemberController.UpdateMemberRequest request, Member member) {
    boolean isFirstUpdate = !member.isVisible();
    
    if (request.getUsername() != null) {
      member.setUsername(request.getUsername());
    }
    
    String avatarUrl = request.getAvatarUrl() != null ? request.getAvatarUrl() : defaultAvatarUrl;
    member.setAvatarUrl(avatarUrl);
    
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
    
    return finalMember;
  }
  
  @Transactional
  public void deleteMember(MemberController.DeleteMemberRequest request, Member member) {
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
    member.setAvatarUrl("https://mybeautip.s3.ap-northeast-2.amazonaws.com/avatar/img_profile_deleted.png");
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
    public void updateDetailInfo(MemberDetailRequest request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new MemberNotFoundException("No such member. id - " + request.getMemberId()));

        member.setBirthday(new Birthday(request.getAgeGroup()));
        memberRepository.save(member);

        Long inviterId = null;
        if (StringUtils.isNotBlank(request.getInviterTag())) {
            inviterId = memberRepository.findByTag(request.getInviterTag())
                    .orElseThrow(() -> new MemberNotFoundException("No such Member. tag - " + request.getInviterTag()))
                    .getId();
        }

        MemberDetail memberDetail = memberDetailRepository.findByMemberId(request.getMemberId())
                .orElse(MemberDetail.builder()
                        .memberId(request.getMemberId())
                        .skinType(request.getSkinType())
                        .skinWorry(request.getSkinWorry())
                        .inviterId(inviterId)
                        .build());

        memberDetail.setSkinType(request.getSkinType());
        memberDetail.setSkinWorry(request.getSkinWorry());
        memberDetail.setInviterId(inviterId);

        memberDetailRepository.save(memberDetail);
    }

    @Transactional
    public String updateAvatar(long memberId, MultipartFile avatar) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("No such memberId - " + memberId));

        String originalAvatar = member.getAvatarUrl();
        String path = "";

        try {
            path = attachmentService.upload(avatar, PATH_AVATAR);
        } catch (IOException ex) {
            throw new MybeautipRuntimeException("Member avatar upload Error. id - " + memberId);
        }

        member.setAvatarUrl(path);
        memberRepository.save(member);

        deleteAvatar(originalAvatar);
        return path;
    }

    public String uploadAvatar(MultipartFile avatar) {
        String path = "";
        try {
            if (avatar != null) {
                path = attachmentService.upload(avatar, PATH_AVATAR);
            } else {
                path = defaultAvatarUrl;
            }

        } catch (IOException ex) {
            throw new MybeautipRuntimeException("Member avatar upload Error.");
        }

        return path;
    }

    public void deleteAvatar(String avatar) {
        attachmentService.deleteAttachments(StringConvertUtil.getPath(avatar));
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
