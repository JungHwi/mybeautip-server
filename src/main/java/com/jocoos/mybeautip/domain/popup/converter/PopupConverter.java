package com.jocoos.mybeautip.domain.popup.converter;

import com.jocoos.mybeautip.domain.popup.dto.PopupButtonResponse;
import com.jocoos.mybeautip.domain.popup.dto.PopupResponse;
import com.jocoos.mybeautip.domain.popup.persistence.domain.Popup;
import com.jocoos.mybeautip.domain.popup.persistence.domain.PopupButton;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

import static com.jocoos.mybeautip.global.code.UrlDirectory.POPUP;
import static com.jocoos.mybeautip.global.util.ImageUrlConvertUtil.toUrl;

@Mapper(componentModel = "spring")
public interface PopupConverter {

    @Mapping(target = "imageUrl", source = "imageFile", qualifiedByName = "convert_popup_image")
    PopupResponse convert(Popup popup);

    @Named("convert_popup_image")
    default String convertToUrl(String file) {
        return toUrl(file, POPUP);
    }

    List<PopupResponse> convert(List<Popup> popupList);

    @Mapping(target = "parameter", source = "linkArgument")
    PopupButtonResponse convertToButton(PopupButton button);

    List<PopupButtonResponse> convertToButton(List<PopupButton> buttonList);
}
