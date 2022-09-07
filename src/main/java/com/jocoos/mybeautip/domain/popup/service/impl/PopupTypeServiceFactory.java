package com.jocoos.mybeautip.domain.popup.service.impl;

import com.jocoos.mybeautip.domain.popup.code.PopupType;
import com.jocoos.mybeautip.domain.popup.service.PopupTypeService;
import com.jocoos.mybeautip.global.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PopupTypeServiceFactory {

    private final SignupPopupService signupPopupService;
    private final ComebackPopupService comebackPopupService;
    private final LoginPopupService loginPopupService;

    public final PopupTypeService getPopupLinkService(PopupType popupType) {
        switch (popupType) {
            case SIGNUP:
                return signupPopupService;
            case COMEBACK:
                return comebackPopupService;
            case LOGIN:
                return loginPopupService;
            default:
                throw new BadRequestException("Request popup is " + popupType.name());
        }
    }
}
