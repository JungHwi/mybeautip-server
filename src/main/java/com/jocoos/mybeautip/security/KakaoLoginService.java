package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.config.Oauth2Config;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import com.jocoos.mybeautip.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoLoginService implements LoginService {
    public static final String PROVIDER_TYPE = "kakao";

    private final Oauth2Config oauth2Config;
    private final Oauth2Client oauth2Client;

    @Override
    public SocialMemberRequest getMember(String code, String state) {
        oauth2Client.setProviderConfig(oauth2Config.getKakao());

        String accessToken = oauth2Client.getAccessToken(code, state);
        log.debug("accessToken: {}", accessToken);

        if (StringUtils.isBlank(accessToken)) {
            throw new BadRequestException(ErrorCode.INVALID_TOKEN, "Failed to get kakao access token.");
        }
        return getUserData(accessToken);
    }

    private SocialMemberRequest getUserData(String accessToken) {
        HashMap<String, Object> body = oauth2Client.getUserData(accessToken);
        return toSocialMember(body);
    }

    @SuppressWarnings("unchecked")
    private SocialMemberRequest toSocialMember(Map<String, Object> attributes) {
        log.debug("{}", attributes);
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
        log.debug("{}", kakaoAccount);
        log.debug("{}", profile);

        return SocialMemberRequest.builder()
                .id(String.valueOf(attributes.get("id")))
                .provider(PROVIDER_TYPE)
                .name((String) profile.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .picture((String) profile.get("profile_image_url"))
                .build();
    }
}
