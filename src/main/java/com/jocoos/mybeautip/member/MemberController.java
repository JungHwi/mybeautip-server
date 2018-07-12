package com.jocoos.mybeautip.member;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.security.Principal;
import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.hateoas.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
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
  public Resource<MemberInfo> getMe(Principal principal) {
    log.debug("member id: {}", principal.getName());
    return memberRepository.findById(Long.parseLong(principal.getName()))
              .map(m -> new Resource<>(new MemberInfo(m)))
              .orElseThrow(() -> new MemberNotFoundException("member_not_found"));
  }

  @PatchMapping(value = "/me", consumes = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<MemberInfo> updateMember(Principal principal,
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

          return new ResponseEntity<>(new MemberInfo(m), HttpStatus.OK);
        })
        .orElseThrow(() -> new MemberNotFoundException(principal.getName()));
  }

  @GetMapping
  @ResponseBody
  public CursorResponse<MemberInfo> getMembers(@RequestParam(defaultValue = "20") int count,
                                               @RequestParam(required = false) String cursor,
                                               @RequestParam(required = false) String keyword) {
    Slice<Member> list = findMembers(keyword, cursor, count);
    ImmutableList<MemberInfo> members = FluentIterable.from(list)
       .transform(m -> new MemberInfo(m))
       .toList();

    String nextCursor = null;
    if (members != null && members.size() > 1) {
      nextCursor = members.get(members.size() - 1).getCreatedAt();
    }

    return new CursorResponse<MemberInfo>(members, keyword, nextCursor, count, "/api/1/members");
  }

  // TODO: Refactor cursor
  @Data
  @NoArgsConstructor
  class CursorResponse<T> {
    private List<T> content;
    private String keyword;
    private int count;
    private String cursor;
    private String nextRef;

    @JsonIgnore
    private String api;
    @JsonIgnore
    private MultiValueMap<String, String> properties = new LinkedMultiValueMap<>();

    public CursorResponse(List<T> content, String keyword, String nextCursor, int count, String api) {
      this.content = content;
      this.keyword = keyword;
      this.count = count;

      this.properties.add("keyword", keyword);
      this.properties.add("count", String.valueOf(count));

      this.cursor = nextCursor;
      if (!Strings.isNullOrEmpty(nextCursor)) {
        this.properties.add("cursor", nextCursor);
      }

      createRef(api);
    }

    private void createRef(String api) {
      String nextRef = UriComponentsBuilder.newInstance()
         .fromUriString(api).queryParams(properties).build().toUriString();
      log.debug("next ref: {}", nextRef);
      this.nextRef = nextRef;
    }
  }

  @AllArgsConstructor
  @Data
  class Cursor<T> {
    private String keyword;
    private T cursor;
    private int count = 20;

    public boolean hasKeyword() {
      return !Strings.isNullOrEmpty(keyword);
    }

    public boolean hasCursor() {
      return cursor != null;
    }
  }

  // TODO: Refactor find member to MemberService
  private Slice<Member> findMembers(String keyword, String cursor, int count) {
    Cursor<Date> dateCursor = null;

    if (Strings.isNullOrEmpty(cursor)) {
      dateCursor = new Cursor<Date>(keyword, null, count);
    } else {
      Date toDate = null;
      try {
        toDate = new Date(Long.parseLong(cursor));
        dateCursor = new Cursor<Date>(keyword, toDate, count);
      } catch (NumberFormatException e) {
        log.error("Cannot convert cursor to Date", e);
        throw new BadRequestException("invalid cursor type");
      }
    }

    return findMembers(dateCursor);
  }

  private Slice<Member> findMembers(Cursor<Date> cursor) {
    Slice<Member> list = null;
    log.debug("Cursor: {}", cursor);

    PageRequest page = PageRequest.of(0, cursor.getCount(), new Sort(Sort.Direction.DESC, "createdAt"));

    if (cursor.hasCursor() && cursor.hasKeyword()) {
      list = memberRepository.findMembersByKeywordAndCursor(cursor.getKeyword(), cursor.getCursor(), page);
    } else if (cursor.hasCursor()) {
      list = memberRepository.findMembersByCursor(cursor.getCursor(), page);
    } else if (cursor.hasKeyword()) {
      list = memberRepository.findMembersByKeyword(cursor.getKeyword(), page);
    } else {
      list = memberRepository.findMembers(page);
    }

    return list;
  }

  @GetMapping("/{id:.+}")
  public MemberInfo getMember(@PathVariable Long id) {
    return memberRepository.findById(id).map(m -> new MemberInfo(m))
        .orElseThrow(() -> new MemberNotFoundException(id));
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
    private String email;
    private String avatarUrl;
    private String intro;
    private String createdAt;

    public MemberInfo(Member member) {
      this.id = member.getId();
      this.username = member.getUsername();
      this.avatarUrl = Strings.isNullOrEmpty(member.getAvatarUrl()) ? "" : member.getAvatarUrl();
      this.email = member.getEmail();
      this.intro = Strings.isNullOrEmpty(member.getIntro()) ? "" : member.getIntro();
      this.createdAt = String.valueOf(member.getCreatedAt().getTime());
    }
  }
}
