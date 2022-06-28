package com.jocoos.mybeautip.domain.popup.service.impl;

import com.jocoos.mybeautip.domain.popup.service.PopupTypeService;
import com.jocoos.mybeautip.member.Member;
import com.jocoos.mybeautip.support.DateUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class SignupPopupService implements PopupTypeService {

    @Override
    public boolean isPopup(Member member) {
        boolean isPopup = false;

        if (member != null) {
            LocalDate localDate = DateUtils.toLocalDate(member.getCreatedAt());
            if (localDate.equals(LocalDate.now())) {
                isPopup = true;
            }
        }
        return isPopup;
    }
}
