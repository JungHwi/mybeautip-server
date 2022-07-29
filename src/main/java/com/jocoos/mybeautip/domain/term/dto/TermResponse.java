package com.jocoos.mybeautip.domain.term.dto;

import com.jocoos.mybeautip.domain.term.code.TermStatus;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TermResponse {

    private final long id;

    private final String title;

    private final TermStatus currentTermStatus;

}
