package com.jocoos.mybeautip.restapi.dto;

import lombok.Data;

import java.util.List;

@Data
public class HealthCheckResponse {

    private List<NoticeInfo> content;
}
