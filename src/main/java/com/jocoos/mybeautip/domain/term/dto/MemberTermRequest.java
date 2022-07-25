package com.jocoos.mybeautip.domain.term.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MemberTermRequest {

    private final long termId;

    private final Boolean isAccept;

}
