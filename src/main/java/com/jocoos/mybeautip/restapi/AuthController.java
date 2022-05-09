package com.jocoos.mybeautip.restapi;

import com.jocoos.mybeautip.member.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

import javax.validation.constraints.NotNull;

import com.jocoos.mybeautip.exception.MybeautipRuntimeException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.security.AccessTokenResponse;
import com.jocoos.mybeautip.security.JwtTokenProvider;
import com.jocoos.mybeautip.security.SocialLoginService;

import org.apache.commons.lang3.StringUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/1/token")
public class AuthController {

  private final MemberService memberService;
  private final SocialLoginService socialLoginService;
  private final JwtTokenProvider jwtTokenProvider;


  @PostMapping(value = "/{provider}", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
  public ResponseEntity<?> socialLogin(@PathVariable String provider,
                                       OauthRequest request) throws UnsupportedEncodingException {
    log.debug("{}, {}", provider, request);
    if (StringUtils.isBlank(request.getRedirectUri())) {
      throw new IllegalArgumentException("Redirect uri was required");
    }

    Member member = socialLoginService.loadMember(provider, request.getCode(), request.getState(), request.getRedirectUri());
    AccessTokenResponse accessTokenResponse = jwtTokenProvider.auth(member);
    log.debug("response: {}", accessTokenResponse);

    String accessToken = accessTokenResponse.getAccessToken();

    boolean validated = jwtTokenProvider.validateToken(accessToken);
    log.debug("validated: {}", validated);

    String memberId = jwtTokenProvider.getMemberId(accessToken);
    log.debug("memberId: {}", memberId);

    memberService.updateLastLoginAt();

    return new ResponseEntity<>(accessTokenResponse, HttpStatus.OK);
  }

  @Data
  static class OauthRequest {
    @NotNull
    String code;
    String state = "mybeautip-web-mobile";
    @NotNull
    String redirectUri;

    public void setRedirect_uri(String redirectUri) {
      this.redirectUri = redirectUri;
    }
  }
}
