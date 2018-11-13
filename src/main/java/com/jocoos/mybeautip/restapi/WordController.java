package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.member.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.regex.Pattern;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/words", produces = MediaType.APPLICATION_JSON_VALUE)
public class WordController {

  
  private final MemberService memberService;

  public WordController(MemberService memberService) {
    this.memberService = memberService;
  }

  @GetMapping
  public void isValidParams(@RequestParam(required = false) String username,
                              @RequestParam(required = false) String email) {
    if (username == null && email == null) {
      throw new BadRequestException("invalid_query_string");
    }
    
    if (email != null) {
      memberService.checkEmailValidation(email);
    }
  
    if (username != null) {
      memberService.checkUsernameValidation(username);
    }
  }
}