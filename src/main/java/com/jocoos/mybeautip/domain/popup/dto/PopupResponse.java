package com.jocoos.mybeautip.domain.popup.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PopupResponse {

    private long id;

    private String imageUrl;

    private List<PopupButtonResponse> buttonList;
}
