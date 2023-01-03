package com.jocoos.mybeautip.domain.popupnotice.dto;

import com.jocoos.mybeautip.domain.popupnotice.code.PopupNoticeLinkType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class PopupNoticeResponse {

    private final Long id;

    private final String imageUrl;

    private final PopupNoticeLinkType linkType;

    private final String parameter;

}
