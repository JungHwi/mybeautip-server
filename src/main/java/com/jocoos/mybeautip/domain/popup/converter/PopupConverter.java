package com.jocoos.mybeautip.domain.popup.converter;

import com.jocoos.mybeautip.domain.popup.dto.PopupButtonResponse;
import com.jocoos.mybeautip.domain.popup.dto.PopupResponse;
import com.jocoos.mybeautip.domain.popup.persistence.domain.Popup;
import com.jocoos.mybeautip.domain.popup.persistence.domain.PopupButton;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PopupConverter {

    PopupResponse convert(Popup popup);

    List<PopupResponse> convert(List<Popup> popupList);

    PopupButtonResponse convertToButton(PopupButton button);

    List<PopupButtonResponse> convertToButton(List<PopupButton> buttonList);
}
