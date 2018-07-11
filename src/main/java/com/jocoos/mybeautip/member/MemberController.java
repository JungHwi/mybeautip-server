package com.jocoos.mybeautip.member;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.security.Principal;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.google.common.collect.FluentIterable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.exception.MemberNotFoundException;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/members", produces = MediaType.APPLICATION_JSON_VALUE)
public class MemberController {

  private final MemberRepository memberRepository;

  public MemberController(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
  }

  @GetMapping("/me")
  public Resource<Member> getMe(Principal principal) {
    log.debug("member id: {}", principal.getName());
    return memberRepository.findById(Long.parseLong(principal.getName()))
              .map(m -> new Resource<>(m))
              .orElseThrow(() -> new MemberNotFoundException("member_not_found"));
  }

  @PatchMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Member> updateMember(Principal principal,
                                             @Valid @RequestBody UpdateMemberRequest updateMemberRequest,
                                             BindingResult bindingResult) {

    log.debug("member id: {}", principal.getName());
    log.debug("binding result: {}", bindingResult);

    if (bindingResult.hasErrors()) {
      log.debug("bindingResult: {}", bindingResult);
      throw new BadRequestException("invalid member request");
    }

    return memberRepository.findById(Long.parseLong(principal.getName()))
        .map(m -> {
          BeanUtils.copyProperties(updateMemberRequest, m);
          memberRepository.save(m);
          return new ResponseEntity<Member>(m, HttpStatus.OK);
        })
        .orElseThrow(() -> new MemberNotFoundException(principal.getName()));
  }

  @GetMapping
  @ResponseBody
  public List<MemberInfo> getMembers() {

    //FIXME: Add keyword and pagination with cursor
    List<Member> list = memberRepository.findAll();

    return FluentIterable.from(list)
        .transform(m -> new MemberInfo(m))
        .toList();
  }

  @GetMapping("/{id:.+}")
  public MemberInfo getMember(@PathVariable Long id) {
    return memberRepository.findById(id).map(m -> new MemberInfo(m))
        .orElseThrow(() -> new MemberNotFoundException());
  }

  @NoArgsConstructor
  @Data
  public static class UpdateMemberRequest {

    @Size(max = 50)
    @NotNull
    private String username;

    @Size(max = 50)
    @NotNull
    private String email;

    @Size(max = 100)
    private String avatarUrl;

    @Size(max = 200)
    private String intro;
  }

  @Data
  public static class MemberInfo {
    private Long id;
    private String username;
    private String avatarUrl;
    private String email;
    private String intro;

    public MemberInfo(Member member) {
      this.id = member.getId();
      this.username = member.getUsername();
      this.avatarUrl = member.getAvatarUrl();
      this.email = member.getEmail();
      this.intro = member.getIntro();
    }
  }
}
