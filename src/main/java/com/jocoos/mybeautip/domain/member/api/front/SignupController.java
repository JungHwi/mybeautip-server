package com.jocoos.mybeautip.domain.member.api.front;

import com.jocoos.mybeautip.domain.member.dto.MemberEntireInfo;
import com.jocoos.mybeautip.domain.member.service.MemberSignupService;
import com.jocoos.mybeautip.restapi.dto.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/1/member/signup")
@RestController
@RequiredArgsConstructor
public class SignupController {

    private final MemberSignupService memberSignupService;

    @PostMapping("")
    public ResponseEntity<MemberEntireInfo> signup(@RequestBody SignupRequest request) {
        MemberEntireInfo result = memberSignupService.signup(request);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}