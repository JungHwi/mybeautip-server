package com.jocoos.mybeautip.restapi;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import com.jocoos.mybeautip.exception.BadRequestException;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.word.BannedWordService;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/words", produces = MediaType.APPLICATION_JSON_VALUE)
public class WordController {

  private final BannedWordService bannedWordService;
  private final MemberRepository memberRepository;

  public WordController(BannedWordService bannedWordService,
                        MemberRepository memberRepository) {
    this.bannedWordService = bannedWordService;
    this.memberRepository = memberRepository;
  }

  @GetMapping
  public void checkUsernameValidation(@RequestParam String username) {
    if (username.length() < 2 || username.length() > 25) {
      throw new BadRequestException(UsernameErrorCode.INVALID_LENGTH);
    }

    if (StringUtils.isNumeric(username)) {
      throw new BadRequestException(UsernameErrorCode.INVALID_NUMBER);
    }

    String regex = "^[\\w가-힣!_~]+$";
    if (!(username.matches(regex))) {
      throw new BadRequestException(UsernameErrorCode.INVALID_CHAR);
    }

    if (memberRepository.countByUsernameAndDeletedAtIsNull(username) > 0) {
      throw new BadRequestException(UsernameErrorCode.ALREADY_USED);
    }

    bannedWordService.findWordAndThrowException(username);
  }

  @Getter
  @AllArgsConstructor
  public enum UsernameErrorCode {
    INVALID_LENGTH("invalid_length", "Valid username length is between 2 and 25."),
    INVALID_NUMBER("invalid_number", "Username should contain at least one character."),
    INVALID_CHAR("invalid_char", "Username contains illegal characters."),
    BANNED_WORD("banned_word", "Username contains banned words."),
    ALREADY_USED("already_used", "Username is already used.");

    private final String error;
    private final String errorDescription;
  }
}