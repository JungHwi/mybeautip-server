package com.jocoos.mybeautip.restapi;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import com.jocoos.mybeautip.member.MemberService;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/words", produces = MediaType.APPLICATION_JSON_VALUE)
public class WordController {

  private final MemberService memberService;

  public WordController(MemberService memberService) {
    this.memberService = memberService;
  }

  @GetMapping
  public void isValidUsername(@RequestParam String username) {
    memberService.checkUsernameValidation(username);
  }
}