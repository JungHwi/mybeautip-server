package com.jocoos.mybeautip.member;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;
import com.jocoos.mybeautip.word.BannedWordService;

@Slf4j
@Service
public class MemberService {

  private final BannedWordService bannedWordService;
  private final MemberRepository memberRepository;
  private final FollowingRepository followingRepository;

  public MemberService(BannedWordService bannedWordService,
                       MemberRepository memberRepository,
                       FollowingRepository followingRepository) {
    this.bannedWordService = bannedWordService;
    this.memberRepository = memberRepository;
    this.followingRepository = followingRepository;
  }

  public Long currentMemberId() {
    return Optional.ofNullable(currentMember()).map(Member::getId).orElse(null);
  }

  public String getGuestUserName() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }

    Object principal = authentication.getPrincipal();
    if (principal instanceof MyBeautipUserDetails) {
      return ((MyBeautipUserDetails) principal).getUsername();
    } else {
      log.warn("Unknown principal type");
    }
    return null;
  }

  public Member currentMember() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !authentication.isAuthenticated()) {
      return null;
    }

    log.debug("authentication: {}", authentication);
    Object principal = authentication.getPrincipal();
    if (principal instanceof MyBeautipUserDetails) {
      return ((MyBeautipUserDetails) principal).getMember();
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
    return new MemberInfo(member, getFollowingId(member));
  }

  public void checkUsernameValidation(@RequestParam String username) {
    if (username.length() < 2 || username.length() > 25) {
      throw new BadRequestException(UsernameErrorCode.INVALID_LENGTH);
    }

    if (StringUtils.isNumeric(username)) {
      throw new BadRequestException(UsernameErrorCode.INVALID_NUMBER);
    }

    String regex = "^[\\w가-힣!_~]+$";
    if (!(username.matches(regex))) {
      throw new BadRequestException(UsernameErrorCode.INVALID_CHAR);
    }

    if (memberRepository.countByUsernameAndDeletedAtIsNull(username) > 0) {
      throw new BadRequestException(UsernameErrorCode.ALREADY_USED);
    }

    bannedWordService.findWordAndThrowException(username);
  }

  @Getter
  @AllArgsConstructor
  public enum UsernameErrorCode {
    INVALID_LENGTH("invalid_length", "Valid username length is between 2 and 25."),
    INVALID_NUMBER("invalid_number", "Username should contain at least one character."),
    INVALID_CHAR("invalid_char", "Username contains illegal characters."),
    BANNED_WORD("banned_word", "Username contains banned words."),
    ALREADY_USED("already_used", "Username is already used.");

    private final String error;
    private final String errorDescription;
  }
}
