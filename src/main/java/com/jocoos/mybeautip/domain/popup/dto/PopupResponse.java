package com.jocoos.mybeautip.domain.popup.dto;

import com.jocoos.mybeautip.domain.popup.code.PopupDisplayType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PopupResponse {

    private long id;

    private String imageUrl;

    private PopupDisplayType displayType;

    private List<PopupButtonResponse> buttonList;
}
