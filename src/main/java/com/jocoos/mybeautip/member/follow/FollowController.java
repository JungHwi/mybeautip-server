package com.jocoos.mybeautip.member.follow;

import com.jocoos.mybeautip.restapi.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Optional;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class FollowController {
  private static FollowService followService;
  private static FollowRepository followRepository;
  
  public FollowController(FollowRepository followRepository, FollowService followService) {
    this.followService = followService;
    this.followRepository = followRepository;
  }
  
  /**
   * Follow {id}
   * @param principal
   * @param id
   */
  @PostMapping("/api/1/members/me/follow/{id}")
  public void followMember(Principal principal, @PathVariable("id") String id) {
    // TODO: Validation, i == you, throw exception
    log.debug("follow " + principal.getName() + ": " + id);
  
    Optional<Follow> optional
        = followRepository.findByIAndYou(Long.parseLong(principal.getName()), Long.parseLong(id));
    
    if (optional.isPresent()) {
      log.debug("Already follow");
    } else {
      Follow follow = new Follow(principal.getName(), id);
      followRepository.save(follow);
    }
  }
  
  /**
   * Unfollow {id}
   * @param principal
   * @param id
   */
  @DeleteMapping("/api/1/members/me/follow/{id}")
  public void unfollowMember(Principal principal, @PathVariable("id") String id) {
    // TODO: Validation, i == you, throw exception
    log.debug("unFollow " + principal.getName() + ": " + id);
    
    Optional<Follow> optional
        = followRepository.findByIAndYou(Long.parseLong(principal.getName()), Long.parseLong(id));
    if (optional.isPresent()) {
      followRepository.delete(optional.get());
    } else {
      log.debug("Already unfollow");
    }
  }
  
  /**
   * Retruns whether I following {id}
   * @param principal
   * @param id
   * @return
   */
  @GetMapping("/api/1/members/me/follow/{id}")
  public ResponseEntity<Response> didFollow(Principal principal, @PathVariable("id") String id) {
    log.debug("did follow? " + principal.getName() + ": " + id);
    Response response = new Response();
    Optional<Follow> optional
        = followRepository.findByIAndYou(Long.parseLong(principal.getName()), Long.parseLong(id));
    
    response.setFollow(optional.isPresent());
    return ResponseEntity.ok(response);
  }
  
  /**
   * Get Member list who I'm following
   * @param principal
   * @return
   */
  @GetMapping("/api/1/members/me/following")
  public ResponseEntity<Response> getFollowing(Principal principal,
                                                FollowListRequest request,
                                                HttpServletRequest httpServletRequest) {
    Response response = followService.getFollowings(httpServletRequest.getRequestURI(),
        Long.parseLong(principal.getName()), request.getCursor(), request.getCount());
    return ResponseEntity.ok(response);
  }
  
  /**
   * Get Member list who follow me
   * @param principal
   * @return
   */
  @GetMapping("/api/1/members/me/followers")
  public ResponseEntity<Response> getFollowers(Principal principal,
                                               FollowListRequest request,
                                               HttpServletRequest httpServletRequest) {
    Response response = followService.getFollowers(httpServletRequest.getRequestURI(),
        Long.parseLong(principal.getName()), request.getCursor(), request.getCount());
    return ResponseEntity.ok(response);
  }
  
  /**
   * Get Member list who {id} follows
   * @param id
   */
  @GetMapping("/api/1/members/{id}/following")
  public ResponseEntity<Response> getFollowing(@PathVariable("id") String id,
                                                FollowListRequest request,
                                                HttpServletRequest httpServletRequest) {
    Response response = followService.getFollowings(httpServletRequest.getRequestURI(),
        Long.parseLong(id), request.getCursor(), request.getCount());
    return ResponseEntity.ok(response);
  }
  
  /**
   * Get Member list who follow {id}
   * @param id
   */
  @GetMapping("/api/1/members/{id}/followers")
  public ResponseEntity<Response> getFollowers(@PathVariable("id") String id,
                                               FollowListRequest request,
                                               HttpServletRequest httpServletRequest) {
    Response response = followService.getFollowers(httpServletRequest.getRequestURI(),
        Long.parseLong(id), request.getCursor(), request.getCount());
    return ResponseEntity.ok(response);
  }
}