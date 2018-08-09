package com.jocoos.mybeautip.member;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.internal.util.StringUtils;

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

      String username = ((User) authentication.getPrincipal()).getUsername();
      if (StringUtils.isNumeric(username)) {
        Long userId = Long.parseLong(username);

        return memberRepository.findById(userId)
           .map(m -> m.getId())
           .orElseThrow(() -> new MemberNotFoundException(userId));
      } else {
        log.warn("user id can't convert to number: {}", username);
      }
    }

    return null;
  }

  public Long getFollowingId(Long you) {
    Optional<Following> optional
      = followingRepository.findByMemberMeIdAndMemberYouId(currentMemberId(), you);
    return optional.map(Following::getId).orElse(null);
  }

  public Long getFollowingId(Member member) {
    Optional<Following> optional
      = followingRepository.findByMemberMeIdAndMemberYouId(currentMemberId(), member.getId());
    return optional.map(Following::getId).orElse(null);
  }
}
