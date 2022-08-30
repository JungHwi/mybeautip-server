package com.jocoos.mybeautip.domain.term.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TermRequest {

    private final long id;

    private final boolean isAccept;

}
