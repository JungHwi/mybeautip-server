package com.jocoos.mybeautip.member.following;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.NotFoundException;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.restapi.CursorResponse;
import com.jocoos.mybeautip.restapi.Response;

@RestController
@RequestMapping(value = "/api/1/members", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class FollowingController {

  private final MemberService memberService;
  private final FollowingService followingService;
  private final MemberRepository memberRepository;
  private final FollowingRepository followingRepository;
  
  public FollowingController(MemberService memberService,
                             FollowingService followingService,
                             MemberRepository memberRepository,
                             FollowingRepository followingRepository) {
    this.memberService = memberService;
    this.followingService = followingService;
    this.memberRepository = memberRepository;
    this.followingRepository = followingRepository;
  }


  @PostMapping("/me/followings")
  public Response followMember(@Valid @RequestBody FollowingMemberRequest followingMemberRequest,
                               BindingResult bindingResult) {
    if (bindingResult.hasErrors()) {
      log.debug("bindingResult: {}", bindingResult);
      throw new BadRequestException("invalid followings request");
    }

    long me = memberService.currentMemberId();
    long you = followingMemberRequest.getMemberId();
    
    if (me == you) {
      throw new BadRequestException("Can't follow myself");
    }
    
    Optional<Following> optional = followingRepository.findByMemberMeIdAndMemberYouId(me, you);
    Response response = new Response();
    
    if (optional.isPresent()) {
      log.debug("Already followed");
      response.setId(optional.get().getId());
    } else {
      Following following = followingRepository.save(
        new Following(memberRepository.getOne(me), memberRepository.getOne(you)));
      response.setId(following.getId());
    }
    return response;
  }
  
  @DeleteMapping("/me/followings/{id}")
  public void unFollowMember(@PathVariable("id") Long id) {
    Optional<Following> optional = followingRepository.findById(id);

    if (optional.isPresent()) {
      if (!memberService.currentMemberId().equals(optional.get().getMemberMe().getId())) {
        throw new BadRequestException("Invalid following id: " + id);
      }
      followingRepository.delete(optional.get());
    } else {
      throw new NotFoundException("following_not_found", "following not found, id: " + id);
    }
  }
  
  @GetMapping("/me/followings")
  public CursorResponse getFollowing(@RequestParam(defaultValue = "50") int count,
                                     @RequestParam(required = false) String cursor,
                                     HttpServletRequest httpServletRequest) {
    return followingService.getFollowings(httpServletRequest.getRequestURI(),
        memberService.currentMemberId(), cursor, count);
  }

  @GetMapping("/me/followers")
  public CursorResponse getFollowers(@RequestParam(defaultValue = "50") int count,
                                     @RequestParam(required = false) String cursor,
                                     HttpServletRequest httpServletRequest) {
    return followingService.getFollowers(httpServletRequest.getRequestURI(),
        memberService.currentMemberId(), cursor, count);
  }

  @GetMapping("/{id}/followings")
  public CursorResponse getFollowing(@PathVariable("id") Long id,
                                     @RequestParam(defaultValue = "50") int count,
                                     @RequestParam(required = false) String cursor,
                                     HttpServletRequest httpServletRequest) {
    return followingService.getFollowings(httpServletRequest.getRequestURI(), id, cursor, count);
  }
  
  @GetMapping("/{id}/followers")
  public CursorResponse getFollowers(@PathVariable("id") Long id,
                                     @RequestParam(defaultValue = "50") int count,
                                     @RequestParam(required = false) String cursor,
                                     HttpServletRequest httpServletRequest) {
    return followingService.getFollowers(httpServletRequest.getRequestURI(), id, cursor, count);
  }
}