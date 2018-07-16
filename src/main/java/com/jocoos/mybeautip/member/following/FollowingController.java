package com.jocoos.mybeautip.member.following;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MybeautipRuntimeException;
import com.jocoos.mybeautip.restapi.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/1/members", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class FollowingController {
  private static FollowingService followingService;
  private static FollowingRepository followingRepository;
  
  public FollowingController(FollowingService followingService,
                             FollowingRepository followingRepository) {
    this.followingService = followingService;
    this.followingRepository = followingRepository;
  }

  @PostMapping("/me/followings")
  public Response followMember(Principal principal,
                               @Valid @RequestBody FollowingMemberRequest followingMemberRequest) {
    // TODO: will be replaced common method
    long me = Long.parseLong(principal.getName());
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
  public void unfollowMember(Principal principal, @PathVariable("id") Long id) {
    Optional<Following> optional = followingRepository.findById(id);
    if (!optional.isPresent()) {
      throw new MybeautipRuntimeException("Access denied", "Can't unfollow");
    } else {
      followingRepository.delete(optional.get());
    }
  }
  
  @GetMapping("/me/followings")
  public ResponseEntity<Response> getFollowing(Principal principal,
                                               FollowingListRequest request,
                                               HttpServletRequest httpServletRequest) {
    Response response = followingService.getFollowings(httpServletRequest.getRequestURI(),
        Long.parseLong(principal.getName()), request.getCursor(), request.getCount());
    return ResponseEntity.ok(response);
  }

  @GetMapping("/me/followers")
  public ResponseEntity<Response> getFollowers(Principal principal,
                                               FollowingListRequest request,
                                               HttpServletRequest httpServletRequest) {
    Response response = followingService.getFollowers(httpServletRequest.getRequestURI(),
        Long.parseLong(principal.getName()), request.getCursor(), request.getCount());
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