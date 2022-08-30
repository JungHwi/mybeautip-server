package com.jocoos.mybeautip.domain.community.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReportRequest {

    private Boolean isReport;
    private String description;
}
