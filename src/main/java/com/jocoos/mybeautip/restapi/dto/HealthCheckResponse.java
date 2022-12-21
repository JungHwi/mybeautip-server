package com.jocoos.mybeautip.restapi.dto;

import com.jocoos.mybeautip.domain.popup.dto.PopupResponse;
import lombok.Data;

import java.util.List;

@Data
public class HealthCheckResponse {
    private String latestVersion;

    private List<NoticeInfo> content;

    private List<PopupResponse> popupList;


}
