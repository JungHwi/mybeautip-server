package com.jocoos.mybeautip.domain.term.dto;

import com.jocoos.mybeautip.domain.term.code.TermStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TermDetailResponse {

    private final long id;

    private final String title;

    private final String content;

    private final TermStatus currentTermStatus;

    private final float version;

}
