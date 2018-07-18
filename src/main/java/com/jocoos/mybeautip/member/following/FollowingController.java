package com.jocoos.mybeautip.member.following;

import com.jocoos.mybeautip.exception.AccessDeniedException;
import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MybeautipRuntimeException;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.restapi.Response;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/1/members", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class FollowingController {
  private static MemberService memberService;
  private static FollowingService followingService;
  private static FollowingRepository followingRepository;
  
  public FollowingController(MemberService memberService,
                             FollowingService followingService,
                             FollowingRepository followingRepository) {
    this.memberService = memberService;
    this.followingService = followingService;
    this.followingRepository = followingRepository;
  }

  @PostMapping("/me/followings")
  public Response followMember(@Valid @NonNull @RequestBody FollowingMemberRequest followingMemberRequest,
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
    
    Optional<Following> optional = followingRepository.findByMeAndYou(me, you);
    Response response = new Response();
    
    if (optional.isPresent()) {
      log.debug("Already followed");
      response.setId(optional.get().getId());
    } else {
      Following following = followingRepository.save(new Following(me, you));
      response.setId(following.getId());
    }
    return response;
  }
  
  @DeleteMapping("/me/followings/{id}")
  public void unfollowMember(@PathVariable("id") Long id) {
    Optional<Following> optional = followingRepository.findById(id);
    if (!optional.isPresent()) {
      throw new AccessDeniedException("Can't unfollow");
    } else {
      followingRepository.delete(optional.get());
    }
  }
  
  @GetMapping("/me/followings")
  public ResponseEntity<Response> getFollowing(FollowingListRequest request,
                                               HttpServletRequest httpServletRequest) {
    Response response = followingService.getFollowings(httpServletRequest.getRequestURI(),
        memberService.currentMemberId(), request.getCursor(), request.getCount());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/me/followers")
  public ResponseEntity<Response> getFollowers(FollowingListRequest request,
                                               HttpServletRequest httpServletRequest) {
    Response response = followingService.getFollowers(httpServletRequest.getRequestURI(),
        memberService.currentMemberId(), request.getCursor(), request.getCount());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}/followings")
  public ResponseEntity<Response> getFollowing(@PathVariable("id") String id,
                                               FollowingListRequest request,
                                               HttpServletRequest httpServletRequest) {
    Response response = followingService.getFollowings(httpServletRequest.getRequestURI(),
        Long.parseLong(id), request.getCursor(), request.getCount());
    return ResponseEntity.ok(response);
  }
  
  @GetMapping("/{id}/followers")
  public ResponseEntity<Response> getFollowers(@PathVariable("id") String id,
                                               FollowingListRequest request,
                                               HttpServletRequest httpServletRequest) {
    Response response = followingService.getFollowers(httpServletRequest.getRequestURI(),
        Long.parseLong(id), request.getCursor(), request.getCount());
    return ResponseEntity.ok(response);
  }
}