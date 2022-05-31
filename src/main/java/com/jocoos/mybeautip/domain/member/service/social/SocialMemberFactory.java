package com.jocoos.mybeautip.domain.member.service.social;

import com.jocoos.mybeautip.domain.member.service.SocialMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SocialMemberFactory {

    private final NaverSocialMemberService naverSocialMemberService;
    private final KakaoSocialMemberService kakaoSocialMemberService;
    private final AppleSocialMemberService appleSocialMemberService;
    private final FacebookSocialMemberService facebookSocialMemberService;

    public SocialMemberService getSocialMemberService(String provider) {
        switch (provider) {
            case "naver" :
                return naverSocialMemberService;
            case "kakao" :
                return kakaoSocialMemberService;
            case "apple" :
                return appleSocialMemberService;
            case "facebook" :
                return facebookSocialMemberService;
            default:
                throw new IllegalArgumentException("Unknown grant type");
        }
    }
}
