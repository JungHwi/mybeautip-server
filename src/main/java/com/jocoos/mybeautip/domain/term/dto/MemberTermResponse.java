package com.jocoos.mybeautip.domain.term.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberTermResponse {

    private final long memberId;

    private final long termId;

    private final boolean isAccept;

}
