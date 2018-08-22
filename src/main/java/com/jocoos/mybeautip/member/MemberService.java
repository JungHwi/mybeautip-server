package com.jocoos.mybeautip.member;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;

@Slf4j
@Service
public class MemberService {

  private final MemberRepository memberRepository;
  private final FollowingRepository followingRepository;

  public MemberService(MemberRepository memberRepository,
                       FollowingRepository followingRepository) {
    this.memberRepository = memberRepository;
    this.followingRepository = followingRepository;
  }

  public Long currentMemberId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication != null) {
      log.debug("authentication: {}", authentication);
      Object principal = authentication.getPrincipal();
      if (principal instanceof UserDetails) {
        String username = ((User) authentication.getPrincipal()).getUsername();
        if (StringUtils.isNumeric(username)) {
          Long userId = Long.parseLong(username);

          return memberRepository.findById(userId)
             .map(m -> m.getId())
             .orElseThrow(() -> new MemberNotFoundException(userId));
        } else {
          log.warn("user id can't convert to number: {}", username);
        }
      } else {
        log.warn("Unknown principal type");
      }
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
}
