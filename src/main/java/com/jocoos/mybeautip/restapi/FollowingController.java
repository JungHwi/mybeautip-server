package com.jocoos.mybeautip.restapi;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingMemberRequest;
import com.jocoos.mybeautip.member.following.FollowingRepository;

@RestController
@RequestMapping(value = "/api/1/members", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class FollowingController {

  private final MemberService memberService;
  private final MemberRepository memberRepository;
  private final FollowingRepository followingRepository;
  
  public FollowingController(MemberService memberService,
                             MemberRepository memberRepository,
                             FollowingRepository followingRepository) {
    this.memberService = memberService;
    this.memberRepository = memberRepository;
    this.followingRepository = followingRepository;
  }

  @Transactional
  @PostMapping("/me/followings")
  public FollowingResponse followMember(@Valid @RequestBody FollowingMemberRequest followingMemberRequest,
                               BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      log.debug("bindingResult: {}", bindingResult);
      throw new BadRequestException("invalid followings request");
    }

    Long me = memberService.currentMemberId();
    if (me == null) {
      throw new MemberNotFoundException("Login required");
    }
    long you = followingMemberRequest.getMemberId();
    
    if (me == you) {
      throw new BadRequestException("Can't follow myself");
    }

    if (!memberRepository.existsById(you)) {
      throw new MemberNotFoundException(you);
    }
    
    Optional<Following> optional = followingRepository.findByMemberMeIdAndMemberYouId(me, you);
    
    if (optional.isPresent()) {
      log.debug("Already followed");
      return new FollowingResponse(optional.get().getId());
    } else {
      Following following = followingRepository.save(
        new Following(memberRepository.getOne(me), memberRepository.getOne(you)));
      memberRepository.updateFollowingCount(me, 1);
      memberRepository.updateFollowerCount(you, 1);
      return new FollowingResponse(following.getId());
    }
  }

  @Transactional
  @DeleteMapping("/me/followings/{id}")
  public void unFollowMember(@PathVariable("id") Long id) {
    Optional<Following> optional = followingRepository.findById(id);

    if (optional.isPresent()) {
      if (!memberService.currentMemberId().equals(optional.get().getMemberMe().getId())) {
        throw new BadRequestException("Invalid following id: " + id);
      }
      followingRepository.delete(optional.get());
      memberRepository.updateFollowingCount(memberService.currentMemberId(), -1);
      memberRepository.updateFollowerCount(optional.get().getMemberYou().getId(), -1);
    } else {
      throw new NotFoundException("following_not_found", "following not found, id: " + id);
    }
  }
  
  @GetMapping("/me/followings")
  public CursorResponse getFollowing(@RequestParam(defaultValue = "50") int count,
                                     @RequestParam(required = false) String cursor,
                                     HttpServletRequest httpServletRequest) {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }
    return getFollowings(httpServletRequest.getRequestURI(), memberId, cursor, count);
  }

  @GetMapping("/me/followers")
  public CursorResponse getFollowers(@RequestParam(defaultValue = "50") int count,
                                     @RequestParam(required = false) String cursor,
                                     HttpServletRequest httpServletRequest) {
    Long memberId = memberService.currentMemberId();
    if (memberId == null) {
      throw new MemberNotFoundException("Login required");
    }
    return getFollowers(httpServletRequest.getRequestURI(), memberId, cursor, count);
  }

  @GetMapping("/{id}/followings")
  public CursorResponse getFollowing(@PathVariable("id") Long id,
                                     @RequestParam(defaultValue = "50") int count,
                                     @RequestParam(required = false) String cursor,
                                     HttpServletRequest httpServletRequest) {
    return getFollowings(httpServletRequest.getRequestURI(), id, cursor, count);
  }
  
  @GetMapping("/{id}/followers")
  public CursorResponse getFollowers(@PathVariable("id") Long id,
                                     @RequestParam(defaultValue = "50") int count,
                                     @RequestParam(required = false) String cursor,
                                     HttpServletRequest httpServletRequest) {
    return getFollowers(httpServletRequest.getRequestURI(), id, cursor, count);
  }


  private CursorResponse getFollowings(String requestUri, long me, String cursor, int count) {
    Date startCursor = (Strings.isBlank(cursor)) ?
      new Date(System.currentTimeMillis()) : new Date(Long.parseLong(cursor));

    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
    Slice<Following> slice = followingRepository.findByCreatedAtBeforeAndMemberMeId(startCursor, me, pageable);

    List<MemberInfo> result = new ArrayList<>();

    for (Following following : slice.getContent()) {
      // Add following id when I follow you
      result.add(new MemberInfo(following.getMemberYou(), memberService.getFollowingId(following.getMemberYou())));
    }

    if (result.size() > 0) {
      String nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
      return new CursorResponse.Builder<>(requestUri, result)
        .withCount(count)
        .withCursor(nextCursor).toBuild();
    } else {
      return new CursorResponse.Builder<>(requestUri, result).toBuild();
    }
  }

  private CursorResponse getFollowers(String requestUri, long you, String cursor, int count) {
    Date startCursor = (Strings.isBlank(cursor)) ?
      new Date(System.currentTimeMillis()) : new Date(Long.parseLong(cursor));

    PageRequest pageable = PageRequest.of(0, count, new Sort(Sort.Direction.DESC, "id"));
    Slice<Following> slice = followingRepository.findByCreatedAtBeforeAndMemberYouId(startCursor, you, pageable);

    List<MemberInfo> result = new ArrayList<>();

    for (Following follower : slice.getContent()) {
      // Add following id when I follow
      result.add(new MemberInfo(follower.getMemberMe(), memberService.getFollowingId(follower.getMemberMe())));
    }

    if (result.size() > 0) {
      String nextCursor = String.valueOf(result.get(result.size() - 1).getCreatedAt().getTime());
      return new CursorResponse.Builder<>(requestUri, result)
        .withCount(count)
        .withCursor(nextCursor).toBuild();
    } else {
      return new CursorResponse.Builder<>(requestUri, result).toBuild();
    }
  }

  @Data
  @AllArgsConstructor
  class FollowingResponse {
    Long id;
  }
}