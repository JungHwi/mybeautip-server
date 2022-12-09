package com.jocoos.mybeautip.domain.member.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionMemberResponse {
    private long memberId;

    private String date;
}
