package com.jocoos.mybeautip.domain.popup.service;

import com.jocoos.mybeautip.domain.popup.converter.PopupConverter;
import com.jocoos.mybeautip.domain.popup.dto.PopupResponse;
import com.jocoos.mybeautip.domain.popup.persistence.domain.Popup;
import com.jocoos.mybeautip.domain.popup.persistence.repository.PopupRepository;
import com.jocoos.mybeautip.domain.popup.service.impl.PopupTypeServiceFactory;
import com.jocoos.mybeautip.member.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PopupService {

    private final PopupRepository repository;
    private final PopupConverter converter;
    private final PopupTypeServiceFactory popupTypeServiceFactory;

    @Transactional
    public List<PopupResponse> getPopup(Member member) {
        List<Popup> popupList = repository.findByActivePopup();
        List<PopupResponse> result = new ArrayList<>();
        for (Popup popup : popupList) {
            PopupTypeService popupLinkService = popupTypeServiceFactory.getPopupLinkService(popup.getType());
            if (popupLinkService.isPopup(member)) {
                result.add(converter.convert(popup));
            }
        }

        return result;
    }
}


