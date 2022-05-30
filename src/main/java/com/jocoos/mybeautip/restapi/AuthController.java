package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.domain.member.dto.OauthRequest;
import com.jocoos.mybeautip.security.SocialLoginService;
import com.jocoos.mybeautip.security.WebSocialLoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/1/token")
public class AuthController {

    private final SocialLoginService socialLoginService;

    @PostMapping(value = "/{provider}", consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<WebSocialLoginResponse> socialLogin(@PathVariable String provider,
                                         OauthRequest request) {

        WebSocialLoginResponse result = socialLoginService.webSocialLogin(provider, request.getCode(), request.getState());

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
