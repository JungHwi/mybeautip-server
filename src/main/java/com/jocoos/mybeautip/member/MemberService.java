package com.jocoos.mybeautip.member;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.log.MemberLeaveLog;
import com.jocoos.mybeautip.log.MemberLeaveLogRepository;
import com.jocoos.mybeautip.member.coupon.CouponService;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.member.report.Report;
import com.jocoos.mybeautip.member.report.ReportRepository;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.restapi.MemberController;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import com.jocoos.mybeautip.tag.TagService;
import com.jocoos.mybeautip.video.Video;
import com.jocoos.mybeautip.word.BannedWordService;

@Slf4j
@Service
public class MemberService {

  private final BannedWordService bannedWordService;
  private final MessageService messageService;
  private final TagService tagService;
  private final MemberRepository memberRepository;
  private final FollowingRepository followingRepository;
  private final ReportRepository reportRepository;
  private final FacebookMemberRepository facebookMemberRepository;
  private final KakaoMemberRepository kakaoMemberRepository;
  private final NaverMemberRepository naverMemberRepository;
  private final AppleMemberRepository appleMemberRepository;
  private final MemberLeaveLogRepository memberLeaveLogRepository;
  private final CouponService couponService;
  
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
                       FollowingRepository followingRepository,
                       ReportRepository reportRepository,
                       FacebookMemberRepository facebookMemberRepository,
                       KakaoMemberRepository kakaoMemberRepository,
                       NaverMemberRepository naverMemberRepository,
                       AppleMemberRepository appleMemberRepository, MemberLeaveLogRepository memberLeaveLogRepository,
                       CouponService couponService) {
    this.bannedWordService = bannedWordService;
    this.messageService = messageService;
    this.tagService = tagService;
    this.memberRepository = memberRepository;
    this.followingRepository = followingRepository;
    this.reportRepository = reportRepository;
    this.facebookMemberRepository = facebookMemberRepository;
    this.kakaoMemberRepository = kakaoMemberRepository;
    this.naverMemberRepository = naverMemberRepository;
    this.appleMemberRepository = appleMemberRepository;
    this.memberLeaveLogRepository = memberLeaveLogRepository;
    this.couponService = couponService;
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
       .orElseThrow(() -> new MemberNotFoundException("Cann't find admin"));
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
  public void updateLastLoginAt() {
    Member member = currentMember();
    if (member != null) {
      memberRepository.updateLastLoginAt(member.getId());
    }
  }
  
  @Transactional
  public Report reportMember(Member me, long targetId, int reasonCode, String reason, Video video, String lang) {
    Member you = memberRepository.findByIdAndDeletedAtIsNull(targetId)
        .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));
    
    Report report = reportRepository.save(new Report(me, you, reasonCode, reason, video));
    memberRepository.updateReportCount(you.getId(), 1);

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
    
    if (request.getEmail() != null) {
      member.setEmail(request.getEmail());
    }
  
    if (request.getAvatarUrl() != null) {
      if ("".equals(request.getAvatarUrl())) {
        member.setAvatarUrl(defaultAvatarUrl);
      } else {
        member.setAvatarUrl(request.getAvatarUrl());
      }
    }
    
    if (request.getIntro() != null) {
      tagService.touchRefCount(request.getIntro());
      tagService.updateHistory(member.getIntro(), request.getIntro(), TagService.TAG_MEMBER, member.getId(), member);
      member.setIntro(request.getIntro());
    }
  
    if (request.getPushable() != null) {
      member.setPushable(request.getPushable());
    }
  
    member.setVisible(true);
    Member finalMember = memberRepository.save(member);
    
    if (isFirstUpdate) { // when first called
      // Follow Admin member as default
      memberRepository.findByUsernameAndLinkAndDeletedAtIsNull("마이뷰팁", 0)
          .ifPresent(adminMember -> {
            followMember(finalMember, adminMember);
            finalMember.setFollowingCount(1); // for response view
          });

      couponService.sendWelcomeCoupon(member);

      couponService.sendEventCoupon(member);
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
}
