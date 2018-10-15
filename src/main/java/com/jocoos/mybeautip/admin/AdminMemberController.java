package com.jocoos.mybeautip.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.MemberNotFoundException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberInfo;
import com.jocoos.mybeautip.member.MemberRoleInfo;
import com.jocoos.mybeautip.member.MemberService;
import com.jocoos.mybeautip.security.MyBeautipUserDetails;

@Slf4j
@RestController
@RequestMapping("/api/admin/me")
public class AdminMemberController {

  private final MemberService memberService;

  public AdminMemberController(MemberService memberService) {
    this.memberService = memberService;
  }

  @GetMapping
  public ResponseEntity<MemberRoleInfo> getMe() {
    MyBeautipUserDetails myBeautipUserDetails = memberService.currentUserDetails();

    GrantedAuthority authority = myBeautipUserDetails.getAuthorities().stream().findAny().orElseThrow(() ->
       new MemberNotFoundException("role not found"));

    MemberRoleInfo memberRoleInfo = null;
    switch (authority.getAuthority()) {
      case "ROLE_ADMIN": {
        memberRoleInfo = new MemberRoleInfo(myBeautipUserDetails.getMember(), 0);
        break;
      }
      case "ROLE_STORE": {
        memberRoleInfo = new MemberRoleInfo(myBeautipUserDetails.getMember(), 1);
        break;
      }
      default:
        throw new MemberNotFoundException("role not found");
    }

    return new ResponseEntity<>(memberRoleInfo, HttpStatus.OK);
  }
}
