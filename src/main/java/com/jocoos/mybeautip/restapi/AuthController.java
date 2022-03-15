package com.jocoos.mybeautip.restapi;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;

import com.jocoos.mybeautip.exception.MybeautipRuntimeException;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.member.MemberRepository;
import com.jocoos.mybeautip.security.AccessTokenResponse;
import com.jocoos.mybeautip.security.JwtTokenProvider;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/1/token")
public class AuthController {

  private final JwtTokenProvider jwtTokenProvider;
  private final MemberRepository memberRepository;

  @PostMapping("/kakao")
  public ResponseEntity<?> authKakao(OauthRequest request) throws UnsupportedEncodingException {
    log.debug("{}", request);

//    Member member = socialLoginService.getMember(request.getProvider(), request.getCode());
    Member member = memberRepository.findById(43L)
        .orElseThrow(() -> new MybeautipRuntimeException("member_not_found"));
    AccessTokenResponse accessTokenResponse = jwtTokenProvider.auth(member);
    log.debug("response: {}", accessTokenResponse);

    String accessToken = accessTokenResponse.getAccessToken();

    boolean validated = jwtTokenProvider.validateToken(accessToken);
    log.debug("validated: {}", validated);

    String memberId = jwtTokenProvider.getMemberId(accessToken);
    log.debug("memberId: {}", memberId);

    return new ResponseEntity<>(accessTokenResponse, HttpStatus.OK);
  }

  @Data
  static class OauthRequest {
    String provider;
    String code;
  }
}
