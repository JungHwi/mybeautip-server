package com.jocoos.mybeautip.domain.popup.service;

import com.jocoos.mybeautip.domain.popup.code.PopupStatus;
import com.jocoos.mybeautip.domain.popup.code.PopupType;
import com.jocoos.mybeautip.domain.popup.converter.PopupConverter;
import com.jocoos.mybeautip.domain.popup.dto.PopupResponse;
import com.jocoos.mybeautip.domain.popup.persistence.domain.Popup;
import com.jocoos.mybeautip.domain.popup.persistence.repository.PopupRepository;
import com.jocoos.mybeautip.domain.popup.service.impl.PopupTypeServiceFactory;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PopupService {

    private final PopupRepository repository;
    private final PopupConverter converter;
    private final PopupTypeServiceFactory popupTypeServiceFactory;

    @Transactional
    public PopupResponse getPopup(Member member) {
        List<Popup> popupList = repository.findByActivePopup();

        for (Popup popup : popupList) {
            PopupTypeService popupLinkService = popupTypeServiceFactory.getPopupLinkService(popup.getType());
            if (popupLinkService.isPopup(member)) {
                return converter.convert(popup);
            }
        }

        return null;
    }

    @Transactional(readOnly = true)
    public PopupResponse getWakeupPopup() {
        Popup popup = repository.findTopByTypeAndStatus(PopupType.WAKEUP, PopupStatus.ACTIVE);
        return converter.convert(popup);
    }
}


