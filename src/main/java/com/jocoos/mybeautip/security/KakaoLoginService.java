package com.jocoos.mybeautip.security;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import com.jocoos.mybeautip.config.Oauth2Config;
import com.jocoos.mybeautip.exception.AuthenticationException;

import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoLoginService implements LoginService {
  public static final String PROVIDER_TYPE = "kakao";

  private final Oauth2Config oauth2Config;
  private final Oauth2Client oauth2Client;

  @Override
  public SocialMember getMember(String code, String state, String redirectUri) {
    oauth2Client.setProviderConfig(oauth2Config.getKakao());

    String accessToken = oauth2Client.getAccessToken(code, state, redirectUri);
    log.debug("accessToken: {}", accessToken);

    if (Strings.isNullOrEmpty(accessToken)) {
      throw new AuthenticationException("Access token required");
    }
    return getUserData(accessToken);
  }

  private SocialMember getUserData(String accessToken) {
    HashMap<String, Object> body = oauth2Client.getUserData(accessToken);
    return toSocialMember(body);
  }

  @SuppressWarnings("unchecked")
  private SocialMember toSocialMember(Map<String, Object> attributes) {
    log.debug("{}", attributes);
    Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
    Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
    log.debug("{}", kakaoAccount);
    log.debug("{}", profile);

    return SocialMember.builder()
        .id(String.valueOf(attributes.get("id")))
        .provider(PROVIDER_TYPE)
        .name((String) profile.get("nickname"))
        .email((String) kakaoAccount.get("email"))
        .picture((String) profile.get("profile_image_url"))
        .build();
  }
}
