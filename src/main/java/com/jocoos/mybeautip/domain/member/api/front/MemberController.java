package com.jocoos.mybeautip.domain.member.api.front;


import com.jocoos.mybeautip.member.LegacyMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final LegacyMemberService legacyMemberService;

    @GetMapping("/1/member/check/phone")
    public ResponseEntity checkPhoneNumber(@RequestParam("phone_number") String phoneNumber) {
        legacyMemberService.validPhoneNumber(phoneNumber);

        return new ResponseEntity(HttpStatus.OK);
    }

}
