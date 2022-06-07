package com.jocoos.mybeautip.domain.member.api.front;

import com.jocoos.mybeautip.domain.member.dto.MemberEntireInfo;
import com.jocoos.mybeautip.domain.member.service.MemberSignupService;
import com.jocoos.mybeautip.restapi.dto.SignupRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/api/")
@RestController
@RequiredArgsConstructor
public class SignupController {

    private final MemberSignupService memberSignupService;

    @PostMapping("/1/member/signup")
    public ResponseEntity<MemberEntireInfo> signup(@RequestBody @Valid SignupRequest request) {
        MemberEntireInfo result = memberSignupService.signup(request);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @DeleteMapping("/1/member")
    public ResponseEntity withdrawal(@RequestBody String reason) {
        memberSignupService.withdrawal(reason);

        return new ResponseEntity(HttpStatus.OK);
    }
}