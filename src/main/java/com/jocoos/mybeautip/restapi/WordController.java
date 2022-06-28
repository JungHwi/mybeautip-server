package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.member.LegacyMemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/1/words", produces = MediaType.APPLICATION_JSON_VALUE)
@Deprecated
public class WordController {


    private final LegacyMemberService legacyMemberService;

    public WordController(LegacyMemberService legacyMemberService) {
        this.legacyMemberService = legacyMemberService;
    }

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
            legacyMemberService.checkUsernameValidation(username, lang);
        }
    }
}