package com.jocoos.mybeautip.admin;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.member.MemberRoleInfo;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.member.following.Following;
import com.jocoos.mybeautip.member.following.FollowingRepository;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;

@Slf4j
@RestController
@RequestMapping("/api/admin")
public class AdminMemberController {

  private final MemberService memberService;
  private final PasswordEncoder passwordEncoder;
  private final AdminMemberRepository adminMemberRepository;
  private final MemberRepository memberRepository;
  private final FollowingRepository followingRepository;

  public AdminMemberController(MemberService memberService,
                               PasswordEncoder passwordEncoder,
                               AdminMemberRepository adminMemberRepository,
                               MemberRepository memberRepository,
                               FollowingRepository followingRepository) {
    this.memberService = memberService;
    this.passwordEncoder = passwordEncoder;
    this.adminMemberRepository = adminMemberRepository;
    this.memberRepository = memberRepository;
    this.followingRepository = followingRepository;
  }

  @GetMapping("/me")
  public ResponseEntity<MemberRoleInfo> getMe() {
    MyBeautipUserDetails myBeautipUserDetails = memberService.currentUserDetails();

    GrantedAuthority authority = myBeautipUserDetails.getAuthorities().stream().findAny().orElseThrow(() ->
       new MemberNotFoundException("role not found"));

    MemberRoleInfo memberRoleInfo;
    switch (authority.getAuthority()) {
      case "ROLE_ADMIN": {
        memberRoleInfo = new MemberRoleInfo(myBeautipUserDetails.getMember(), 0);
        break;
      }
      case "ROLE_STORE": {
        memberRoleInfo = new MemberRoleInfo(myBeautipUserDetails.getMember(), 1);
        AdminMember adminMember = adminMemberRepository.findByMemberId(memberRoleInfo.getId())
           .orElseThrow(() -> new MemberNotFoundException("member_not_found", "invalid member id"));

        if (adminMember.getStore() != null) {
          memberRoleInfo.setStoreId(adminMember.getStore().getId());
        }
        break;
      }
      default:
        throw new MemberNotFoundException("role not found");
    }

    return new ResponseEntity<>(memberRoleInfo, HttpStatus.OK);
  }

  @PostMapping(value = "/admins", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity saveAdmin(@Valid @RequestBody CreateAdminRequest request,
                                  BindingResult bindingResult) {
    log.debug("request: {}", request);

    if (bindingResult.hasErrors()) {
      throw new BadRequestException(bindingResult.getFieldError());
    }

    if (adminMemberRepository.existsByEmail(request.getEmail())) {
      throw new BadRequestException("email_duplicated", "invalid email");
    }

    AdminMember adminMember = new AdminMember();
    BeanUtils.copyProperties(request, adminMember);

    adminMember.setPassword(passwordEncoder.encode(request.getPassword()));

    Member member = memberRepository.findById(request.getMemberId()).orElseThrow(() -> new MemberNotFoundException("member_not_found", "invalid member_id"));
    adminMember.setMember(member);
    adminMember.setCreatedAt(new Date());

    adminMemberRepository.save(adminMember);

    log.debug("{}", adminMember);
    return new ResponseEntity(HttpStatus.CREATED);
  }
  
  @PostMapping("/members/followings")
  public void followMember(@Valid @RequestBody AdminFollowingMemberRequest request) {
    Member me = memberRepository.findByIdAndDeletedAtIsNull(request.getMe())
        .orElseThrow(() -> new MemberNotFoundException(request.getMe()));
  
    Member you = memberRepository.findByIdAndDeletedAtIsNull(request.getYou())
        .orElseThrow(() -> new MemberNotFoundException(request.getYou()));
    
    if (request.getMe().equals(request.getYou())) {
      throw new BadRequestException("bad_request", "Bad request - me and you cannot be the same.");
    }
    
    Following following = followingRepository.findByMemberMeIdAndMemberYouId(me.getId(), you.getId()).orElse(null);
    
    if (following == null) {
      memberService.followMember(me, you);
    } else {  // Already followed
      throw new BadRequestException("bad_request", "Already follow");
    }
  }
  
  @Data
  public static class AdminFollowingMemberRequest {
    @NotNull
    private Long me;
  
    @NotNull
    private Long you;
  }

  @Data
  public static class CreateAdminRequest {

    @NotNull @Size(max = 50)
    private String email;
    @NotNull @Size(max = 60)
    private String password;
    @NotNull
    private Long memberId;
  }

}
