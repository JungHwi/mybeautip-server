package com.jocoos.mybeautip.member;

import javax.transaction.Transactional;
import java.util.Optional;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.member.report.Report;
import com.jocoos.mybeautip.member.report.ReportRepository;
import com.jocoos.mybeautip.notification.MessageService;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import com.jocoos.mybeautip.word.BannedWordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Service
public class MemberService {

  private final BannedWordService bannedWordService;
  private final MessageService messageService;
  private final MemberRepository memberRepository;
  private final FollowingRepository followingRepository;
  private final ReportRepository reportRepository;
  
  private final String emailRegex = "[A-Za-z0-9_-]+[\\.\\+A-Za-z0-9_-]*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})";

  private static final String USERNAME_INVALID_LENGTH = "username.invalid_length";
  private static final String USERNAME_INVALID_FORMAT = "username.invalid_format";
  private static final String USERNAME_INVALID_CHAR = "username.invalid_char";
  private static final String USERNAME_ALREADY_USED = "username.already_used";
  private static final String EMAIL_INVALID_FORMAT = "email.invalid_format";
  private static final String MEMBER_NOT_FOUND = "member.not_found";

  public MemberService(BannedWordService bannedWordService,
                       MessageService messageService,
                       MemberRepository memberRepository,
                       FollowingRepository followingRepository,
                       ReportRepository reportRepository) {
    this.bannedWordService = bannedWordService;
    this.messageService = messageService;
    this.memberRepository = memberRepository;
    this.followingRepository = followingRepository;
    this.reportRepository = reportRepository;
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

  public MemberInfo getMemberInfo(Member member) {
    if (currentMember() != null && member.getId().equals(currentMember().getId())) {
      return new MemberMeInfo(member);
    } else {
      return new MemberInfo(member, getFollowingId(member));
    }
  }

  public void checkUsernameValidation(@RequestParam String username, String lang) {
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

    if (!currentMember().getUsername().equals(username)) {
      if (memberRepository.countByUsernameAndDeletedAtIsNull(username) > 0) {
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
    return ((member.getPermission() & Member.COMMENT_POST) == 0);
  }
  
  public boolean hasLivePostPermission(Member member) {
    return ((member.getPermission() & Member.LIVE_POST) == 0);
  }
  
  public boolean hasMotdPostPermission(Member member) {
    return ((member.getPermission() & Member.MOTD_POST) == 0);
  }
  
  @Transactional
  public void reportMember(Member me, long targetId, String reason, String lang) {
    Member you = memberRepository.findByIdAndDeletedAtIsNull(targetId)
        .orElseThrow(() -> new MemberNotFoundException(messageService.getMessage(MEMBER_NOT_FOUND, lang)));
  
    reportRepository.save(new Report(me, you, reason));
    memberRepository.updateReportCount(you.getId(), 1);
  }
}
