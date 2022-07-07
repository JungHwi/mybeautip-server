package com.jocoos.mybeautip.security;

import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoginServiceFactory {

    private final FacebookLoginService facebookLoginService;
    private final NaverLoginService naverLoginService;
    private final KakaoLoginService kakaoLoginService;
    private final AppleLoginService appleLoginService;

    public LoginService getLoginService(String providerType) {
        switch (providerType) {
            case FacebookLoginService.PROVIDER_TYPE:
                return facebookLoginService;
            case NaverLoginService.PROVIDER_TYPE:
                return naverLoginService;
            case KakaoLoginService.PROVIDER_TYPE:
                return kakaoLoginService;
            case AppleLoginService.PROVIDER_TYPE:
                return appleLoginService;
            default:
                throw new BadRequestException("Not supported provider type. provider - " + providerType);
        }
    }
}
