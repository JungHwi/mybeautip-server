package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.domain.member.service.MemberService;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.LegacyMemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/words", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Deprecated
public class WordController {

    private final MemberService memberService;
    private final LegacyMemberService legacyMemberService;

    @GetMapping
    public void isValidParams(@RequestParam(required = false) String username,
                              @RequestParam(required = false) String email,
                              @RequestHeader(value = "Accept-Language", defaultValue = "ko") String lang) {
        if (username == null && email == null) {
            throw new BadRequestException("invalid_query_string");
        }

        if (email != null) {
            legacyMemberService.checkEmailValidation(email, lang);
        }

        if (username != null) {
            memberService.checkUsernameValidation(username, lang);
        }
    }
}