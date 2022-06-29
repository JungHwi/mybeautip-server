package com.jocoos.mybeautip.domain.popup.dto;

import com.jocoos.mybeautip.domain.popup.code.ButtonLinkType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PopupButtonResponse {

    private String name;

    private ButtonLinkType linkType;

    private String parameter;
}
