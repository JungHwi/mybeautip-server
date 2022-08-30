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
public class FacebookLoginService implements LoginService {
    public static final String PROVIDER_TYPE = "facebook";

    private final Oauth2Config oauth2Config;
    private final Oauth2Client oauth2Client;

    @Override
    public SocialMemberRequest getMember(String code, String state) {
        oauth2Client.setProviderConfig(oauth2Config.getFacebook());

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
        Map<String, Object> picture = (Map<String, Object>) attributes.get("picture");
        Map<String, Object> data = (Map<String, Object>) picture.get("data");
        String url = data.get("url") != null ? String.valueOf(data.get("url")) : "";

        return SocialMemberRequest.builder()
                .id(String.valueOf(attributes.get("id")))
                .provider(PROVIDER_TYPE)
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture(url)
                .build();
    }
}
