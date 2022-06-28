package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.config.Oauth2Config;
import com.jocoos.mybeautip.global.exception.AuthenticationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverLoginService implements LoginService {
    public static final String PROVIDER_TYPE = "naver";

    private final Oauth2Config oauth2Config;
    private final Oauth2Client oauth2Client;

    @Override
    public SocialMemberRequest getMember(String code, String state) {
        oauth2Client.setProviderConfig(oauth2Config.getNaver());

        String accessToken = oauth2Client.getAccessToken(code, state);
        log.debug("accessToken: {}", accessToken);

        if (StringUtils.isBlank(accessToken)) {
            throw new AuthenticationException("Access token required");
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
        Map<String, Object> profile = (Map<String, Object>) attributes.get("response");
        log.debug("{}", profile);

        return SocialMemberRequest.builder()
                .id(String.valueOf(profile.get("id")))
                .provider(PROVIDER_TYPE)
                .name((String) profile.get("nickname"))
                .email((String) profile.get("email"))
                .picture((String) profile.get("profile_image"))
                .build();
    }
}
