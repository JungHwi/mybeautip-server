package com.jocoos.mybeautip.domain.community.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {

    private Boolean isReport;

    private Integer reportCount;

}
