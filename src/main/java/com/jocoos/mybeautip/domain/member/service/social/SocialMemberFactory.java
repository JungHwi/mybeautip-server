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
            case "facebook":
                return facebookSocialMemberService;
            case "naver":
                return naverSocialMemberService;
            case "kakao":
                return kakaoSocialMemberService;
            case "apple":
                return appleSocialMemberService;
            default:
                throw new IllegalArgumentException("Unknown grant type");
        }
    }

    public SocialMemberService getSocialMemberService(int memberLink) {
        switch (memberLink) {
            case 1:
                return facebookSocialMemberService;
            case 2:
                return naverSocialMemberService;
            case 4:
                return kakaoSocialMemberService;
            case 8:
                return appleSocialMemberService;
            default:
                throw new IllegalArgumentException("Unknown member link");
        }
    }
}