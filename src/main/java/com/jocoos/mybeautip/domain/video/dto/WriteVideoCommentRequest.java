package com.jocoos.mybeautip.domain.video.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
public class WriteVideoCommentRequest {

    @NotBlank
    private final String contents;
    private final Long parentId;
}
