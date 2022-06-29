package com.jocoos.mybeautip.domain.popup.service.impl;

import com.jocoos.mybeautip.domain.popup.service.PopupTypeService;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginPopupService implements PopupTypeService {

    @Override
    public boolean isPopup(Member member) {
        boolean isPopup = false;
        if (member != null) {
            isPopup = true;
        }
        return isPopup;
    }
}
